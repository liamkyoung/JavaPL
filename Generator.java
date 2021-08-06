package plc.project;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class Generator implements Ast.Visitor<Void> {

    private final PrintWriter writer;
    private int indent = 0;

    public Generator(PrintWriter writer) {
        this.writer = writer;
    }

    private void print(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Ast) {
                visit((Ast) object);
            } else {
                writer.write(object.toString());
            }
        }
    }

    private void newline(int indent) {
        writer.println();
        for (int i = 0; i < indent; i++) {
            writer.write("    ");
        }
    }

    private void makeMain() {
        String main0 = "public class Main {";
        String main1 = "public static void main(String[] args) {";
        String main2 = "System.exit(new Main().main());";
        String brace = "}";

        writer.write(main0);
        newline(indent);
        newline(++indent);
    writer.write(main1);
        newline(++indent);
        writer.write(main2);
        newline(--indent);
        writer.write(brace);
        newline(--indent);
    }

    @Override
    public Void visit(Ast.Source ast) {
        makeMain();

        if (ast.getFields().size() > 0) {
            indent++;
            for (Ast.Field field : ast.getFields()) {
                newline(indent);
                visit(field);
            }
            newline(--indent);
        }

        if (ast.getMethods().size() > 0) {
            indent++;
            for (Ast.Method method : ast.getMethods()) {
                newline(indent);
                visit(method);
                newline(0);
            }
            indent--;
        }

        newline(indent);

        writer.write("}");

        return null;
    }

    @Override
    public Void visit(Ast.Field ast) {
        Environment.Variable var = ast.getVariable();
        String type = var.getType().getJvmName();
        String name = var.getName();

        writer.write(type + " " + name);

        if (ast.getValue().isPresent()) {
            writer.write(" = ");
            visit(ast.getValue().get());
        }

        writer.write(";");

        return null;
    }

    @Override
    public Void visit(Ast.Method ast) {
        String type = ast.getFunction().getReturnType().getJvmName();
        String name = ast.getName();
        List<String> params = ast.getParameters();
        // paramTypes may be non-jvm type
        List<String> typeList = ast.getParameterTypeNames();
        List<String> paramTypes = new ArrayList<String>();

        for (String s : typeList) {
            paramTypes.add(Environment.getType(s).getJvmName());
        }

        List<Ast.Stmt> stmts = ast.getStatements();
        writer.write(type + " " + name + "(");

        for (int i = 0; i < params.size(); i++) {
            writer.write(paramTypes.get(i) + " " + params.get(i));
            if (i != params.size() - 1) {
                writer.write(", ");
            }
        }
        writer.write(") {");

        if (!stmts.isEmpty()) {
            indent++;
            for (Ast.Stmt stmt : stmts) {
                newline(indent);
                visit(stmt);
            }
            newline(--indent);
        }
        writer.write("}");
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) {
        visit(ast.getExpression());
        writer.write(";");
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) {
        Environment.Variable var = ast.getVariable();
        String jvm = var.getJvmName();
        String type = var.getType().getJvmName();

        writer.write(type + " " + jvm);

        if (ast.getValue().isPresent()) {
            writer.write(" = ");
            visit(ast.getValue().get());
        }
        writer.write(";");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) {
        // Need to check...
        visit(ast.getReceiver());
        writer.write(" = ");
        visit(ast.getValue());
        writer.write(";");
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.If ast) {
        List<Ast.Stmt> tStmts = ast.getThenStatements();
        List<Ast.Stmt> eStmts = ast.getElseStatements();
        writer.write("if (");
        visit(ast.getCondition());
        writer.write(") {");

        indent++;
        for (Ast.Stmt stmt : tStmts) {
            newline(indent);
            visit(stmt);
        }

        newline(--indent);
        writer.write("}");

        if (!eStmts.isEmpty()) {
            writer.write(" else {");
            indent++;
            for (Ast.Stmt stmt : eStmts) {
                newline(indent);
                visit(stmt);
            }
            newline(--indent);
            writer.write("}");
        }

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.For ast) {
        Ast.Expr.Access value = (Ast.Expr.Access) ast.getValue();
        String valueName = value.getVariable().getJvmName();

        String loop = String.format("for (int %s : %s) {", ast.getName(), valueName);
        writer.write(loop);
        indent++;
        for (Ast.Stmt stmt : ast.getStatements()) {
            newline(indent);
            visit(stmt);
        }
        newline(--indent);
        writer.write("}");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.While ast) {
        List<Ast.Stmt> stmts = ast.getStatements();
        writer.write("while (");
        visit(ast.getCondition());
        writer.write(") {");

        if (!stmts.isEmpty()) {
            indent++;
            for (Ast.Stmt stmt : stmts) {
                newline(indent);
                visit(stmt);
            }
            newline(--indent);
        }
        writer.write("}");

        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) {
        writer.write("return ");
        visit(ast.getValue());
        writer.write(";");
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Literal ast) {
        Object literal = ast.getLiteral();
        String litString = literal.toString();

        if (literal instanceof Boolean) {
            if (litString.equals("true")) {
                writer.write("true");
            } else {
                writer.write("false");
            }
        } else if (literal instanceof BigDecimal) {
            BigDecimal dec = new BigDecimal(litString);
            writer.write(dec.toString());
        } else if (literal instanceof BigInteger) {
            BigInteger integer = new BigInteger(litString);
            writer.write(integer.toString());
        } else if (literal instanceof Character) {
            String cha = "\'" + literal.toString() + "\'";
            writer.write(cha);
        } else if (literal instanceof String) {
            String str = "\"" + litString + "\"";
            writer.write(str);
        } else {
            print(literal);
        }
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Group ast) {
        Ast.Expr expr = ast.getExpression();
        writer.write("(");
        visit(expr);
        writer.write(")");
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Binary ast) {
        String op = ast.getOperator();
        Ast.Expr left = ast.getLeft();
        Ast.Expr right = ast.getRight();
        visit(left);
        writer.write(" ");
        switch(op) {
            case "AND":
                op = "&& ";
                writer.write(op);
                break;
            case "OR":
                op = "|| ";
                writer.write(op);
                break;
            default:
                writer.write(op + " ");
                break;
        }
        visit(right);

        return null;
    }

    @Override
    public Void visit(Ast.Expr.Access ast) {
        String jvm = ast.getVariable().getJvmName();

        if (ast.getReceiver().isPresent()) {
            visit(ast.getReceiver().get());
            writer.write(".");
        }
        writer.write(jvm);
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Function ast) {
        String jvm = ast.getFunction().getJvmName();
        List<Ast.Expr> args = ast.getArguments();

        if (ast.getReceiver().isPresent()) {
            visit(ast.getReceiver().get());
            writer.write(".");
        }
        writer.write(jvm);
        writer.write("(");
        for (int i = 0; i < args.size(); i++) {
            visit(args.get(i));
            if (i != args.size() - 1) {
                writer.write(", ");
            }
        }
        writer.write(")");

        return null;
    }

}
