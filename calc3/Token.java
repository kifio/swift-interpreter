package calc3;

record Token(Type type, char[] value) {
    boolean isOperation() {
        return type().ordinal() >= 1 && type().ordinal() < Type.values().length - 1;
    }

    enum Type {
        INTEGER,
        PLUS, MINUS, MUL, DIV,
        EOF;
    }
}
