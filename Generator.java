package plc.project;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Override
    public Void visit(Ast.Source ast) {
        String main = "public static void main(String[] args) {" +
                "System.exit(new Main.main());" +
                "}";
        writer.write(main);
        return null;
    }

    @Override
    public Void visit(Ast.Field ast) {
        return null;
    }

    @Override
    public Void visit(Ast.Method ast) {
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) {
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) {
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) {
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.If ast) {
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.For ast) {
        // String start = "for (int %s : %s)".format(visit(ast.getValue()), ast.getName());
        // writer.write();
        return null;
    }

    @Override
    public Void visit(Ast.Stmt.While ast) {
        List<Ast.Stmt> stmts = ast.getStatements();
        writer.write("while (");
        visit(ast.getCondition());
        writer.write(") {");

        if (!stmts.isEmpty()) {
            for (Ast.Stmt stmt : stmts) {
                newline(1);
                visit(stmt);
            }
            newline(0);
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
        // System.out.println(litString);

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
        } else if (litString.equals("NIL")) {
            writer.write("null");
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
