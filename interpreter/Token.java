package interpreter;

public record Token(Type type, String value) {

    public static final Token NEW_LINE = new Token(Type.NEW_LINE, "\n");
    public static final Token SEMI = new Token(Type.SEMI, ";");
    public static final Token COLON = new Token(Type.COLON, ":");
    public static final Token ASSIGN = new Token(Type.ASSIGN, "=");
    public static final Token PLUS = new Token(Type.PLUS, "+");
    public static final Token MINUS = new Token(Type.MINUS, "-");
    public static final Token MUL = new Token(Type.MUL, "*");
    public static final Token DIV = new Token(Type.DIV, "/");
    public static final Token INTEGER = new Token(Type.INTEGER, "Int");
    public static final Token LPAREN = new Token(Type.INTEGER, "(");
    public static final Token RPAREN = new Token(Type.INTEGER, ")");
    public static final Token DOUBLE = new Token(Type.DOUBLE, "Double");
    public static final Token LET = new Token(Type.DOUBLE, "let");
    public static final Token VAR = new Token(Type.DOUBLE, "var");

    boolean isSumOrSub() {
        return this == PLUS || this == MINUS;
    }

    boolean isMulOrDiv() {
        return this == MUL || this == DIV;
    }

    public enum Type {
        VAR, LET,
        ID,
        ASSIGN,
        NEW_LINE, SEMI, COLON,
        INTEGER, DOUBLE,
        PLUS, MINUS, MUL, DIV,
        LPAREN, RPAREN,
        COMMENT,
        EOF
    }
}
