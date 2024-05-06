package calc6;

record Token(Type type, char[] value) {
    enum Type {
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
