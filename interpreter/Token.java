package interpreter;

record Token(Type type, String value) {
    enum Type {
        VAR, LET,
        ID,
        ASSIGN,
        INTEGER, 
        PLUS, MINUS, MUL, DIV,
        LPAREN, RPAREN,
        EOF;
    }   

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
