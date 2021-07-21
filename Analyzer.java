package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * See the specification for information about what the different visit
 * methods should do.
 */
public final class Analyzer implements Ast.Visitor<Void> {

    public Scope scope;
    private Ast.Method method;

    public Analyzer(Scope parent) {
        scope = new Scope(parent);
        scope.defineFunction("print", "System.out.println", Arrays.asList(Environment.Type.ANY), Environment.Type.NIL, args -> Environment.NIL);
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Void visit(Ast.Source ast) {
        Environment.Function main = scope.lookupFunction("main", 0);
        if (main.getReturnType() != Environment.Type.INTEGER) {
            throw new RuntimeException("Main Function is not type Integer.");
        }

        for (Ast.Field field : ast.getFields()) {
            visit(field);
        }

        for (Ast.Method method : ast.getMethods()) {
            visit(method);
        }

        return null;
    }

    @Override
    public Void visit(Ast.Field ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Method ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Expression ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Declaration ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Assignment ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.If ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.For ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.While ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Stmt.Return ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Expr.Literal ast) {
        Object lit = ast.getLiteral();
        if (lit instanceof Boolean) {
            ast.setType(Environment.Type.BOOLEAN);
        }
        // How to check for NIL ? ***
        else if (lit.equals(Environment.Type.NIL)) {
            ast.setType(Environment.Type.NIL);
        } else if (lit instanceof Character) {
            ast.setType(Environment.Type.CHARACTER);
        } else if (lit instanceof String) {
            ast.setType(Environment.Type.STRING);
        } else if (lit instanceof BigInteger) {
            if (((BigInteger) lit).bitCount() > 32) {
                throw new RuntimeException("Error: Integer value exceeds 32-bit signed int.");
            }
            ast.setType(Environment.Type.INTEGER);
        } else if (lit instanceof BigDecimal) {
            double decVal = ((BigDecimal) lit).doubleValue();
            if (decVal == Double.NEGATIVE_INFINITY || decVal == Double.POSITIVE_INFINITY) {
                throw new RuntimeException("Error: Decimal value was larger than a normal 64-bit double value.");
            }
            ast.setType(Environment.Type.DECIMAL);
        }
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Group ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    @Override
    public Void visit(Ast.Expr.Binary ast) {
        String op = ast.getOperator();
        Ast.Expr left = ast.getLeft();
        Ast.Expr right = ast.getRight();

        String leftType = left.getType().getName();
        String rightType = right.getType().getName();

        switch (op) {
            case "AND":
            case "OR":
                requireAssignable(left.getType(), Environment.Type.BOOLEAN);
                requireAssignable(right.getType(), Environment.Type.BOOLEAN);
                ast.setType(Environment.Type.BOOLEAN);
                break;
            case "<":
            case "<=":
            case ">":
            case ">=":
            case "==":
            case "!=":
                if (left.getType() != right.getType()) {
                    throw new RuntimeException("Error: Left and Right Sides are Incompatible Types.");
                }
                requireAssignable(Environment.Type.COMPARABLE, left.getType());
                requireAssignable(Environment.Type.COMPARABLE, right.getType());
                ast.setType(Environment.Type.BOOLEAN);
                break;
            case "+":
                if (leftType.equals("String") || rightType.equals("String")) {
                    ast.setType(Environment.Type.STRING);
                } else if (leftType.equals("Integer") && rightType.equals("Integer")) {
                    ast.setType(Environment.Type.INTEGER);
                } else if (leftType.equals("Decimal") && rightType.equals("Decimal")) {
                    ast.setType(Environment.Type.DECIMAL);
                } else {
                    throw new RuntimeException("Error: Incompatible types with + Operator.");
                }
                break;
            case "-":
            case "*":
            case "/":
                if (leftType.equals("Integer") && rightType.equals("Integer")) {
                    ast.setType(Environment.Type.INTEGER);
                } else if (leftType.equals("Decimal") && rightType.equals("Decimal")) {
                    ast.setType(Environment.Type.DECIMAL);
                }
                break;
            default:
                throw new RuntimeException("Error: Unexpected Binary Operator");
        }
        return null;
    }

    @Override
    public Void visit(Ast.Expr.Access ast) {
        // Needs work.
        if (ast.getReceiver().isPresent()) {
            Ast.Expr var = ast.getReceiver().get();
            // Probably incorrect
            Environment.Variable variable = scope.lookupVariable(var.toString());
            ast.setVariable(variable);
        } else {
            ast.getVariable();
        }
    }

    @Override
    public Void visit(Ast.Expr.Function ast) {
        throw new UnsupportedOperationException();  // TODO
    }

    public static void requireAssignable(Environment.Type target, Environment.Type type) {
        if (target.getName() == type.getName()) {
            // Matching type.
        } else if (target.getName().equals("ANY")) {
            // target is ANY
        } else if (target.getName().equals("Comparable")) {
            // Comparable
            if (type.getName().equals("Integer")) {

            } else if (type.getName().equals("Decimal")) {

            } else if (type.getName().equals("Character")) {

            } else if (type.getName().equals("String")) {

            } else {
                throw new RuntimeException("Right Hand Side not a Comparable type.");
            }
        } else {
            throw new RuntimeException("Types are not compatible.");
        }
    }

}
