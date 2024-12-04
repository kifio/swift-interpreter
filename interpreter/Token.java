package interpreter;

public record Token(Type type, String value) {

    public static final Token NEW_LINE = new Token(Type.SYMBOL, "\n");
    public static final Token SEMI = new Token(Type.SYMBOL, ";");
    public static final Token COMMA = new Token(Type.SYMBOL, ",");
    public static final Token COLON = new Token(Type.SYMBOL, ":");
    public static final Token ASSIGN = new Token(Type.SYMBOL, "=");
    public static final Token PLUS = new Token(Type.SYMBOL, "+");
    public static final Token MINUS = new Token(Type.SYMBOL, "-");
    public static final Token MUL = new Token(Type.SYMBOL, "*");
    public static final Token DIV = new Token(Type.SYMBOL, "/");
    public static final Token LPAREN = new Token(Type.SYMBOL, "(");
    public static final Token RPAREN = new Token(Type.SYMBOL, ")");
    public static final Token INTEGER = new Token(Type.SYMBOL, "Int");
    public static final Token DOUBLE = new Token(Type.SYMBOL, "Double");
    public static final Token LET = new Token(Type.SYMBOL, "let");
    public static final Token VAR = new Token(Type.SYMBOL, "var");
    public static final Token FUNC = new Token(Type.SYMBOL, "func");
    public static final Token OPENING_CURLY_BRACE = new Token(Type.SYMBOL, "{");
    public static final Token CLOSING_CURLY_BRACE = new Token(Type.SYMBOL, "}");
    public static final Token EOF = new Token(Type.EOF, "");

    boolean isSumOrSub() {
        return this == PLUS || this == MINUS;
    }

    boolean isMulOrDiv() {
        return this == MUL || this == DIV;
    }

    public enum Type {
        SYMBOL, ID, NUMBER, EOF
    }
}
