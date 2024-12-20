package interpreter.ast;

import interpreter.Token;

public record Variable(Token type, Token name, Token valueType) implements AbstractSyntaxTree {
    @Override
    public AbstractSyntaxTree copy() {
        return new Variable(type, name, valueType);
    }
}
