package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Interpreter implements Ast.Visitor<Environment.PlcObject> {

    private Scope scope = new Scope(null);

    public Interpreter(Scope parent) {
        scope = new Scope(parent);
        scope.defineFunction("print", 1, args -> {
            System.out.println(args.get(0).getValue());
            return Environment.NIL;
        });
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Environment.PlcObject visit(Ast.Source ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Field ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Method ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Expression ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Declaration ast) {
        if (ast.getValue().isPresent()) {
            scope.defineVariable(ast.getName(), visit(ast.getValue().get()));
        } else {
            scope.defineVariable(ast.getName(), Environment.NIL);
        }
        return Environment.NIL;
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Assignment ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.If ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.For ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.While ast) {
        while (requireType(Boolean.class, visit(ast.getCondition()))) {
            try {
                scope = new Scope(scope);
                for (Ast.Stmt stmt : ast.getStatements()) {
                    visit(stmt);
                }

            } finally {
                scope = scope.getParent();
            }
        }
        return Environment.NIL;
    }

    @Override
    public Environment.PlcObject visit(Ast.Stmt.Return ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Literal ast) {
        // Check if Literal is BigInt, BigDec, Boolean, String, Null, Character
        // Returns literal value as a PlcObject.
        Object literal = ast.getLiteral();

        if (literal instanceof BigInteger) {
            // Commented out special cases to see if the tests pass correctly
//            if (literal == BigInteger.ZERO) {
//                return Environment.create(BigInteger.ZERO);
//            } else if (literal == BigInteger.ONE) {
//                return Environment.create(BigInteger.ONE);
//            } else if (literal == BigInteger.TEN) {
//                return Environment.create(BigInteger.TEN);
//            }
            return Environment.create(literal);
        } else if (literal instanceof BigDecimal) {
//            if (literal == BigDecimal.ONE) {
//                return Environment.create(BigDecimal.ONE);
//            }
            return Environment.create(literal);
        } else if (literal instanceof Boolean) {
            // Assign TRUE, FALSE
            if (literal == Boolean.TRUE) {
                return Environment.create(true);
            } else {
                return Environment.create(false);
            }
        } else if (literal == null) {
            return Environment.NIL;
        } else if (literal instanceof String) {
            return Environment.create(literal);
        } else if (literal instanceof Character) {
            return Environment.create(literal);
        }
        throw new RuntimeException("Received an unexpected literal type. Cannot process.");
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Group ast) {
        // Group has one expression, sub expression can break up into many parts
        // Allow it to get handled by other visit()'s
        Environment.PlcObject group = visit(ast.getExpression());

        if (group.getValue() == null) {
            throw new RuntimeException("Error: Group was null");
        }
        // Documentation says to return value but method says to return Environment.PlcObject so simply
        // returning the PlcObject with attached value.
        return group;
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Binary ast) {
        Ast.Expr l = ast.getLeft();
        Ast.Expr r = ast.getRight();
        Environment.PlcObject left = visit(l);
        Environment.PlcObject right = visit(r);
        if (left == Environment.NIL || right == Environment.NIL) {
            // End recursion a different way?
            throw new RuntimeException("Error: Left or right binary was equal to null.");
        }

        if (ast.getOperator().equals("+")) {

            // If EITHER is a string, concatenate.
            // Otherwise, if left == decimal, right must be a decimal; same for integers.

            // x + y * 3

            // Check if
            // Check left and right for strings
            // If both fail, check for integers/decimals

        } else if (ast.getOperator().equals("-")) {

        } else if (ast.getOperator().equals("*")) {

        } else if (ast.getOperator().equals("/")) {

        } else if (ast.getOperator().equals("AND")) {

        } else if (ast.getOperator().equals("OR")) {

        } else if (ast.getOperator().equals("<")) {

        } else if (ast.getOperator().equals(">")) {

        } else if (ast.getOperator().equals("<=")) {

        } else if (ast.getOperator().equals(">=")) {

        } else if (ast.getOperator().equals("==")) {

        } else if (ast.getOperator().equals("!=")) {

        }
        throw new RuntimeException("Error: No valid operator");
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Access ast) {
        // Needs work.. Still wrapping my head around these concepts.
        // Access : (Optional(receivers), name)
        // If methods are present...
        if (ast.getReceiver().isPresent()) {
            // Grab list of receivers???
            // Evaluate receiver.
            // Return value of appropriate field.
            return Environment.NIL; // Placeholder.
        } else {
            return scope.lookupVariable(ast.getName()).getValue();
        }
    }

    @Override
    public Environment.PlcObject visit(Ast.Expr.Function ast) {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Helper function to ensure an object is of the appropriate type.
     */
    private static <T> T requireType(Class<T> type, Environment.PlcObject object) {
        if (type.isInstance(object.getValue())) {
            return type.cast(object.getValue());
        } else {
            throw new RuntimeException("Expected type " + type.getName() + ", received " + object.getValue().getClass().getName() + ".");
        }
    }

    /**
     * Exception class for returning values.
     */
    private static class Return extends RuntimeException {

        private final Environment.PlcObject value;

        private Return(Environment.PlcObject value) {
            this.value = value;
        }

    }

}
