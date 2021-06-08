package plc.project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The parser takes the sequence of tokens emitted by the lexer and turns that
 * into a structured representation of the program, called the Abstract Syntax
 * Tree (AST).
 *
 * The parser has a similar architecture to the lexer, just with {@link Token}s
 * instead of characters. As before, {@link #peek(Object...)} and {@link
 * #match(Object...)} are helpers to make the implementation easier.
 *
 * This type of parser is called <em>recursive descent</em>. Each rule in our
 * grammar will have it's own function, and reference to other rules correspond
 * to calling that functions.
 */
public final class Parser {

    private final TokenStream tokens;

    public Parser(List<Token> tokens) {
        this.tokens = new TokenStream(tokens);
    }

    /**
     * Parses the {@code source} rule.
     */
    public Ast.Source parseSource() throws ParseException {
        // parseField, parseMethod
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code field} rule. This method should only be called if the
     * next tokens start a field, aka {@code LET}.
     */
    public Ast.Field parseField() throws ParseException {
        // Notes for part b:
        // Match on LET
        // Peek on Identifier, =, Expression, ;
        // Return new Ast.Field
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code method} rule. This method should only be called if the
     * next tokens start a method, aka {@code DEF}.
     */
    public Ast.Method parseMethod() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code statement} rule and delegates to the necessary method.
     * If the next tokens do not start a declaration, if, while, or return
     * statement, then it is an expression/assignment statement.
     */
    public Ast.Stmt parseStatement() throws ParseException {
        // Parsing for Expressions
        Ast.Expr expr = parseExpression();
        if (match("=")) {
            Ast.Expr expr1 = parseExpression();
            if (match(";")) {
                return new Ast.Stmt.Assignment(expr, expr1);
            }
            throw new ParseException("Error: No semicolon", tokens.get(0).getIndex());
        }
        else if (match(";")) {
            return new Ast.Stmt.Expression(expr);
        }
        throw new ParseException("Error: No semicolon", tokens.get(0).getIndex());
    }

    /**
     * Parses a declaration statement from the {@code statement} rule. This
     * method should only be called if the next tokens start a declaration
     * statement, aka {@code LET}.
     */
    public Ast.Stmt.Declaration parseDeclarationStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses an if statement from the {@code statement} rule. This method
     * should only be called if the next tokens start an if statement, aka
     * {@code IF}.
     */
    public Ast.Stmt.If parseIfStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses a for statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a for statement, aka
     * {@code FOR}.
     */
    public Ast.Stmt.For parseForStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses a while statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a while statement, aka
     * {@code WHILE}.
     */
    public Ast.Stmt.While parseWhileStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses a return statement from the {@code statement} rule. This method
     * should only be called if the next tokens start a return statement, aka
     * {@code RETURN}.
     */
    public Ast.Stmt.Return parseReturnStatement() throws ParseException {
        throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Parses the {@code expression} rule.
     */
    public Ast.Expr parseExpression() throws ParseException {
        return parseLogicalExpression();
    }

    /**
     * Parses the {@code logical-expression} rule.
     */
    public Ast.Expr parseLogicalExpression() throws ParseException {
        Ast.Expr expr = parseEqualityExpression();

        while (peek("AND") || peek("OR")) {
            if (match("AND")) {
                Ast.Expr right = parseEqualityExpression();
                expr = new Ast.Expr.Binary("AND", expr, right);
            }

            else {
                match("OR");
                Ast.Expr right = parseEqualityExpression();
                expr = new Ast.Expr.Binary("OR", expr, right);
            }
        }
        return expr;

    }

    /**
     * Parses the {@code equality-expression} rule.
     */
    public Ast.Expr parseEqualityExpression() throws ParseException {
        Ast.Expr expr = parseAdditiveExpression();

        while (peek("<") || peek("<=") || peek(">") || peek(">=") || peek("==") || peek("!=")) {
            if (match("<")) {
                Ast.Expr right = parseAdditiveExpression();
                expr = new Ast.Expr.Binary("<", expr, right);
            }

            else if (match("<=")){
                Ast.Expr right = parseAdditiveExpression();
                expr = new Ast.Expr.Binary("<=", expr, right);
            }

            else if (match(">")){
                Ast.Expr right = parseAdditiveExpression();
                expr = new Ast.Expr.Binary(">", expr, right);
            }

            else if (match(">=")){
                Ast.Expr right = parseAdditiveExpression();
                expr = new Ast.Expr.Binary(">=", expr, right);
            }

            else if (match("==")){
                Ast.Expr right = parseAdditiveExpression();
                expr = new Ast.Expr.Binary("==", expr, right);
            }

            else {
                match("!=");
                Ast.Expr right = parseEqualityExpression();
                expr = new Ast.Expr.Binary("!=", expr, right);
            }
        }

        return expr;
    }

    /**
     * Parses the {@code additive-expression} rule.
     */
    public Ast.Expr parseAdditiveExpression() throws ParseException {
        Ast.Expr expr = parseMultiplicativeExpression();

        while (peek("+") || peek("-")) {
            if (match("+")) {
                Ast.Expr right = parseMultiplicativeExpression();
                expr = new Ast.Expr.Binary("+", expr, right);
            }

            else {
                match("-");
                Ast.Expr right = parseMultiplicativeExpression();
                expr = new Ast.Expr.Binary("-", expr, right);
            }
        }
        return expr;
    }

    /**
     * Parses the {@code multiplicative-expression} rule.
     */
    public Ast.Expr parseMultiplicativeExpression() throws ParseException {
        Ast.Expr expr = parseSecondaryExpression();

        while (peek("*") || peek("/")) {
            if (match("*")) {
                Ast.Expr right = parseSecondaryExpression();
                expr = new Ast.Expr.Binary("*", expr, right);
            }

            else {
                match("/");
                Ast.Expr right = parseSecondaryExpression();
                expr = new Ast.Expr.Binary("/", expr, right);
            }
        }
        return expr;
    }

    /**
     * Parses the {@code secondary-expression} rule.
     */
    public Ast.Expr parseSecondaryExpression() throws ParseException {
        // Unfinished..
        Ast.Expr expr = parsePrimaryExpression();
        List<Ast.Expr> parameters = new ArrayList<Ast.Expr>();

        while (match(".")) {
            if (match(Token.Type.IDENTIFIER)) {
                String name = tokens.get(-1).getLiteral();
                if (match("(")) {
                    if (match(")")) {
                        return new Ast.Expr.Function(Optional.of(expr), name, parameters);
                    }
                    Ast.Expr param = parseExpression();
                    parameters.add(param);

                    while (match(",")) {
                        Ast.Expr extra_param = parseExpression();
                        parameters.add(extra_param);
                    }
                    if (match(")")) {
                        return new Ast.Expr.Function(Optional.of(expr), name, parameters);
                    }

                    else {
                        throw new ParseException("Error: No closing right parenthesis. \")\"", tokens.get(0).getIndex());
                    }

                }
                else {
                    return new Ast.Expr.Access(Optional.of(expr), name);
                }
            }
            else {
                throw new ParseException("Error: No identifier", tokens.get(0).getIndex());
            }
        }


        return expr;



        }



    /**
     * Parses the {@code primary-expression} rule. This is the top-level rule
     * for expressions and includes literal values, grouping, variables, and
     * functions. It may be helpful to break these up into other methods but is
     * not strictly necessary.
     * @return
     */
    public Ast.Expr parsePrimaryExpression() throws ParseException {
        // Booleans
        if (match("NIL"))   { return new Ast.Expr.Literal(null);  }
        if (match("TRUE"))  { return new Ast.Expr.Literal(true);  }
        if (match("FALSE")) { return new Ast.Expr.Literal(false); }

        // Characters
        if (match(Token.Type.CHARACTER)) {
            // Remove 's & Replace Escape Characters.
            String val = tokens.get(-1).getLiteral();
            val = val.replace("'", "");
            val = val.replace("\\b", "\b");
            val = val.replace("\\n", "\n");
            val = val.replace("\\r", "\r");
            val = val.replace("\\t", "\t");
            val = val.replace("\\'", "\'");
            val = val.replace("\\\\", "\\");
            Character character = val.charAt(0);

            return new Ast.Expr.Literal(character);
        }

        // Strings
        if (match(Token.Type.STRING)) {
            // Remove "s & Replace Escape Characters.
            String val = tokens.get(-1).getLiteral();
            val = val.replace("\"", "");
            val = val.replace("\\b", "\b");
            val = val.replace("\\n", "\n");
            val = val.replace("\\r", "\r");
            val = val.replace("\\t", "\t");
            val = val.replace("\\'", "\'");
            val = val.replace("\\\\", "\\");
            return new Ast.Expr.Literal(val);
        }

        if (match(Token.Type.INTEGER) && !match(Token.Type.DECIMAL)) {
            return new Ast.Expr.Literal(new BigInteger(tokens.get(-1).getLiteral()));
        }

        // Decimals
        if (match(Token.Type.DECIMAL)) {
            return new Ast.Expr.Literal(new BigDecimal(tokens.get(-1).getLiteral()));
        }


        if (match("(")) {
            Ast.Expr expr = parseExpression();

            if (match(")")) {
                return new Ast.Expr.Group(expr);
            }
            else {
                throw new ParseException("Error: No closing right parenthesis. \")\"", tokens.get(0).getIndex());
            }
        }

        // Group Expression

        // Identifier AND/OR Parameters..
        // Needs work.
        if (match(Token.Type.IDENTIFIER)) {
            // String of Identifier Token.
            String name = tokens.get(-1).getLiteral();
            if (match("(")) {
                // Flag variable to check for ")"
                List<Ast.Expr> parameters = new ArrayList<Ast.Expr>();
                //Empty fcn
                if (match(")")) {
                    return new Ast.Expr.Function(Optional.empty(), name, parameters);
                }

                Ast.Expr param = parseExpression();
                parameters.add(param);

                while (match(",")) {
                    Ast.Expr extra_param = parseExpression();
                    parameters.add(extra_param);
                }
                if (match(")")) {
                    return new Ast.Expr.Function(Optional.empty(), name, parameters);
                }

                else {
                    throw new ParseException("Error: No closing right parenthesis. \")\"", tokens.get(0).getIndex());
                }

            }
            // Returning Variable without any ()
            return new Ast.Expr.Access(Optional.empty(), name);
        }

        return new Ast.Expr.Access(Optional.empty(), "");

    }

    /**
     * As in the lexer, returns {@code true} if the current sequence of tokens
     * matches the given patterns. Unlike the lexer, the pattern is not a regex;
     * instead it is either a {@link Token.Type}, which matches if the token's
     * type is the same, or a {@link String}, which matches if the token's
     * literal is the same.
     *
     * In other words, {@code Token(IDENTIFIER, "literal")} is matched by both
     * {@code peek(Token.Type.IDENTIFIER)} and {@code peek("literal")}.
     */
    private boolean peek(Object... patterns) {
        for (int i = 0; i < patterns.length; i++) {
            if (!tokens.has(i)) {
                return false;
            } else if (patterns[i] instanceof Token.Type) {
                if (patterns[i] != tokens.get(i).getType()) {
                    return false;
                }
            } else if (patterns[i] instanceof String) {
                if (!patterns[i].equals(tokens.get(i).getLiteral())) {
                    return false;
                }
            } else {
                throw new AssertionError("Invalid pattern object: " + patterns[i].getClass());
            }
        }
        return true;
    }

    /**
     * As in the lexer, returns {@code true} if {@link #peek(Object...)} is true
     * and advances the token stream.
     */
    private boolean match(Object... patterns) {
        boolean peek = peek(patterns);

        if (peek) {
            for (int i = 0; i < patterns.length; i++) {
                tokens.advance();
            }
        }
        return peek;
    }

    private static final class TokenStream {

        private final List<Token> tokens;
        private int index = 0;

        private TokenStream(List<Token> tokens) {
            this.tokens = tokens;
        }

        /**
         * Returns true if there is a token at index + offset.
         */
        public boolean has(int offset) {
            return index + offset < tokens.size();
        }

        /**
         * Gets the token at index + offset.
         */
        public Token get(int offset) {
            return tokens.get(index + offset);
        }

        /**
         * Advances to the next token, incrementing the index.
         */
        public void advance() {
            index++;
        }

    }

}
