package interpreter.ast;

import interpreter.Token;

public record Number(Token token) implements AbstractSyntaxTree {
    @Override
    public AbstractSyntaxTree copy() {
        return new Number(token);
    }
}
