package calc;

record Token(Type type, char[] value) {
    enum Type {
        INTEGER, PLUS, MINUS, EOF;
    }
}
