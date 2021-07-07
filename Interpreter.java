package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        // Grab left & right
        Object left = visit(ast.getLeft()).getValue();
        Object right = visit(ast.getRight()).getValue();

        if (ast.getOperator().equals("+")) {
            // Concatenation
            if (left instanceof String || right instanceof String) {
                String result = left.toString() + right.toString();
                return Environment.create(result);
            } else if (left instanceof BigInteger) {
                // Right must also be the same type.
                if (right instanceof BigInteger) {
                    BigInteger result = ((BigInteger) left).add((BigInteger) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Incompatible Number Types Trying To Be Added.");
                }
            } else if (left instanceof BigDecimal) {
                // Right must also be the same type.
                if (right instanceof BigDecimal) {
                    BigDecimal result = ((BigDecimal) left).add((BigDecimal) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Incompatible Number Types Trying To Be Added.");
                }
            }
        } else if (ast.getOperator().equals("-")) {
            // If left is BigInt/BigDec, rhs must be same.
            if (left instanceof BigInteger) {
                if (right instanceof BigInteger) {
                    BigInteger result = ((BigInteger) left).subtract((BigInteger) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Second number is an incompatible type (LHS is Integer).");
                }
            } else if (left instanceof BigDecimal) {
                if (right instanceof BigDecimal) {
                    BigDecimal result = ((BigDecimal) left).subtract((BigDecimal) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Second number is an incompatible type (LHS is Decimal).");
                }
            } else {
                throw new RuntimeException("Error: Left side of Binary Expression was not a number.");
            }
        } else if (ast.getOperator().equals("*")) {
            // If left is BigInt/BigDec, rhs must be same.
            if (left instanceof BigInteger) {
                if (right instanceof BigInteger) {
                    BigInteger result = ((BigInteger) left).multiply((BigInteger) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Second number is an incompatible type (LHS is Integer).");
                }
            } else if (left instanceof BigDecimal) {
                if (right instanceof BigDecimal) {
                    BigDecimal result = ((BigDecimal) left).multiply((BigDecimal) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Second number is an incompatible type (LHS is Decimal).");
                }
            } else {
                throw new RuntimeException("Error: Left side of Binary Expression was not a number.");
            }
        } else if (ast.getOperator().equals("/")) {
            // if left is dec/int, rhs must also be same
            if (left instanceof BigInteger && right instanceof BigInteger) {
                if (!right.equals(0)) {
                    BigInteger result = ((BigInteger) left).divide((BigInteger) right);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Denominator is 0. Cannot divide.");
                }

            } else if (left instanceof BigDecimal && right instanceof BigDecimal) {
                if (!right.equals(0)) {
                    BigDecimal result = (((BigDecimal) left).divide((BigDecimal) right)).setScale(0, RoundingMode.HALF_EVEN);
                    return Environment.create(result);
                } else {
                    throw new RuntimeException("Error: Denominator is 0. Cannot divide.");
                }

            } else {
                throw new RuntimeException("Error: Incompatible types when trying to divide.");
            }

        } else if (ast.getOperator().equals("AND")) {
            if (left instanceof Boolean) {
                Boolean l = (Boolean) left;
                if (!l) { // false
                    return Environment.create(false);
                } else if (right instanceof Boolean) {
                    Boolean r = (Boolean) right;
                    if (r) { // left && right
                        return Environment.create(true);
                    } else {
                        return Environment.create(false);
                    }
                } else {
                    throw new RuntimeException("Error: Right Hand Side of AND is not a Boolean.");
                }
            } else {
                throw new RuntimeException("Error: Left Hand Side of AND is not a Boolean.");
            }
        } else if (ast.getOperator().equals("OR")) {
            if (left instanceof Boolean) {
                Boolean l = (Boolean) left;
                if (l) {
                    return Environment.create(true);
                } else if (right instanceof Boolean) {
                    Boolean r = (Boolean) right;
                    if (r) {
                        return Environment.create(true);
                    } else {
                        return Environment.create(false);
                    }
                } else {
                    throw new RuntimeException("Error: Right Hand Side of OR is not a Boolean.");
                }
            } else {
                throw new RuntimeException("Error: Left Hand Side of OR is not a Boolean.");
            }
        } else if (ast.getOperator().equals("<")) {
            if (left instanceof Comparable && right instanceof Comparable) {
                // Comparing...
                if (left instanceof BigDecimal && right instanceof BigDecimal) {
                    BigDecimal l = (BigDecimal) left;
                    BigDecimal r = (BigDecimal) right;
                    int result = l.compareTo(r);
                    // -1 if l < r <- TRUE
                    // 0 if equal <- FALSE
                    // 1 if l > r <- FALSE
                    if (result == -1) {
                        return Environment.create(true);
                    } else {
                        return Environment.create(false);
                    }
                } else if (left instanceof BigInteger && right instanceof BigInteger) {

                } else {

                }

            } else {
                throw new RuntimeException("Error: Binary Expression is not Comparable.");
            }
        } else if (ast.getOperator().equals(">")) {

        } else if (ast.getOperator().equals("<=")) {

        } else if (ast.getOperator().equals(">=")) {

        } else if (ast.getOperator().equals("==")) {
            return Environment.create(Objects.equals(left, right));
        } else if (ast.getOperator().equals("!=")) {
            return Environment.create(!Objects.equals(left, right));
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
