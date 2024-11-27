package interpreter;

public record Token(Type type, String value) {

    public static final Token NEW_LINE = new Token(Type.NEW_LINE, "\n");
    public static final Token SEMI = new Token(Type.SEMI, ";");
    public static final Token COLON = new Token(Type.COLON, ":");

    boolean isSumOrSub() {
        return type() == Type.PLUS || type() == Type.MINUS;
    }

    boolean isMulOrDiv() {
        return type() == Type.MUL || type() == Type.DIV;
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
