package interpreter.ast;

import interpreter.Token;

public record Variable(Token name) implements AbstractSyntaxTree {

    @Override
    public AbstractSyntaxTree copy() {
        return new Variable(name);
    }
}
