package calc2;

record Token(Type type, char[] value) {
    enum Type {
        INTEGER, PLUS, MINUS, MUL, DIV, EOF;
    }
}
