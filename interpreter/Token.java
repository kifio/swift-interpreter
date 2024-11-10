package interpreter;

public record Token(Type type, String value) {

    public enum Type {
        VAR, LET,
        ID,
        ASSIGN,
        NEW_LINE, SEMI,
        INTEGER, DOUBLE,
        INT_TYPE, DOUBLE_TYPE,
        PLUS, MINUS, MUL, DIV,
        LPAREN, RPAREN,
        COMMENT,
        EOF;
    }   

    public static final Token NEW_LINE = new Token(Type.NEW_LINE, "\n");
    public static final Token SEMI = new Token(Type.SEMI, ";");

    boolean isOperation() {
        return type().ordinal() >= 1 && type().ordinal() < Type.values().length - 1;
    }

    boolean isSumOrSub() {
        return type() == Type.PLUS || type() == Type.MINUS;
    }

    boolean isMulOrDiv() {
        return type() == Type.MUL || type() == Type.DIV;
    }
}
