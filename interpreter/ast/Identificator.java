package interpreter.ast;

import interpreter.Token;

public abstract class Identificator implements AbstractSyntaxTree {

    private Token type;
    private Token value;

    protected Identificator(Token type, Token value) {
        this.type = type;
        this.value = value;
    }

    public Token type() {
        return type;
    }

    public Token value() {
        return value;
    }
}
