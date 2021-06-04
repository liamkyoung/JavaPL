package plc.project;

import jdk.nashorn.internal.codegen.types.Type;

import java.util.ArrayList;
import java.util.List;

import static plc.project.Token.Type.*;

/**
 * The lexer works through three main functions:
 *
 *  - {@link #lex()}, which repeatedly calls lexToken() and skips whitespace
 *  - {@link #lexToken()}, which lexes the next token
 *  - {@link CharStream}, which manages the state of the lexer and literals
 *
 * If the lexer fails to parse something (such as an unterminated string) you
 * should throw a {@link ParseException} with an index at the character which is
 * invalid or missing.
 *
 * The {@link #peek(String...)} and {@link #match(String...)} functions are
 * helpers you need to use, they will make the implementation a lot easier.
 */
public final class Lexer {

    private final CharStream chars;

    public Lexer(String input) {
        chars = new CharStream(input);
    }

    /**
     * Repeatedly lexes the input using {@link #lexToken()}, also skipping over
     * whitespace where appropriate.
     */
    public List<Token> lex() {

        List<Token> tokens = new ArrayList<Token>();

        while (chars.has(0)) {

            while (peek(" ") || peek("\b") || peek("\n") || peek("\r") || peek("\t")) {
                lexEscape();
            }
            if (chars.has(0)) {
                chars.skip();
                tokens.add(lexToken());
            }

        }

        return tokens;

    }

    /**
     * This method determines the type of the next token, delegating to the
     * appropriate lex method. As such, it is best for this method to not change
     * the state of the char stream (thus, use peek not match).
     *
     * The next character should start a valid token since whitespace is handled
     * by {@link #lex()}
     */
    public Token lexToken() {

        if (peek("[A-Za-z_]")) {
            return lexIdentifier();
        }

        else if (peek("[+-]", "[0-9]") || peek("[0-9]")) {
            return lexNumber();
        }

        else if (peek("'")) {
            return lexCharacter();
        }

        else if (peek("\"")) {
            return lexString();
        }


        else {
            return lexOperator();
        }


    }

    public Token lexIdentifier() {

        match("[A-Za-z_]");
        while (match("[A-Za-z0-9_-]")) {
        }

        return chars.emit(IDENTIFIER);
    }

    public Token lexNumber() {

        match("[+-]");
        while (match("[0-9]")) {

        }

        if (peek("\\.", "[0-9]")) {
            match("\\.");
            while (match("[0-9]")) {
            }
            return chars.emit(DECIMAL);
        }

        else {
            return chars.emit(INTEGER);
        }
    }

    public Token lexCharacter() {
        match("'");
        if (match("[^'\\n\\r\\\\]")) {
            if (match("'")) {
                return chars.emit(CHARACTER);
            }
            else {
                throw new ParseException("Error: Invalid/missing character!", chars.getIndex());
            }
        }

        else {
            if (match("\\\\", "[bnrt'\"\\\\]")) {
                if (match("'")) {
                    return chars.emit(CHARACTER);
                }
                else {
                    throw new ParseException("Error: Invalid/missing character!", chars.getIndex());
                }
            }
            else {
                throw new ParseException("Error: Invalid/missing character!", chars.getIndex());
            }
        }


    }

    public Token lexString() {

        match("\"");

        if (match("\"")) {
            return chars.emit(STRING);
        }


        if (!peek("[^\"\\n\\r\\\\]") && !peek("\\\\", "[bnrt'\"\\\\]")) {
            if (peek("\\\\")) {
                throw new ParseException("Error: Invalid string!", chars.getIndex() + 1);
            }
            else {
                throw new ParseException("Error: Invalid string!", chars.getIndex());
            }
        }

        while (match("[^\"\\n\\r\\\\]") || match("\\\\", "[bnrt'\"\\\\]")) {

        }

        if (match("\"")) {
            return chars.emit(STRING);
        }

        else {
            if (peek("\\\\")) {
                throw new ParseException("Error: Invalid string!", chars.getIndex() + 1);
            }
            else {
                throw new ParseException("Error: Invalid string!", chars.getIndex());
            }
        }




    }

    public void lexEscape() {
        chars.advance();
    }

    public Token lexOperator() {
       if (match("[<>!=]", "=")) {
           return chars.emit(OPERATOR);
       }

           else {
               match("[\\s\\S]");
               return chars.emit(OPERATOR);
           }

    }

    /**
     * Returns true if the next sequence of characters match the given patterns,
     * which should be a regex. For example, {@code peek("a", "b", "c")} would
     * return true if the next characters are {@code 'a', 'b', 'c'}.
     */
    public boolean peek(String... patterns) {

        for (int i = 0; i < patterns.length; i++) {

            if ( !chars.has(i) || !String.valueOf(chars.get(i)).matches(patterns[i]) ) {

                return false;
            }

        }
        return true;
    }

    /**
     * Returns true in the same way as {@link #peek(String...)}, but also
     * advances the character stream past all matched characters if peek returns
     * true. Hint - it's easiest to have this method simply call peek.
     */
    public boolean match(String... patterns) {

        boolean peek = peek(patterns);

        if (peek) {

            for (int i = 0; i < patterns.length; i++){
                chars.advance();
            }

        }
        return peek;
    }

    /**
     * A helper class maintaining the input string, current index of the char
     * stream, and the current length of the token being matched.
     *
     * You should rely on peek/match for state management in nearly all cases.
     * The only field you need to access is {@link #index} for any {@link
     * ParseException} which is thrown.
     */
    public static final class CharStream {

        private final String input;
        private int index = 0;
        private int length = 0;

        public CharStream(String input) {
            this.input = input;
        }

        public boolean has(int offset) {
            return index + offset < input.length();
        }

        public char get(int offset) {
            return input.charAt(index + offset);
        }

        public int getIndex() {
            return index;
        }

        public void advance() {
            index++;
            length++;
        }

        public void skip() {
            length = 0;
        }

        public Token emit(Token.Type type) {
            int start = index - length;
            skip();
            return new Token(type, input.substring(start, index), start);
        }

    }

}
