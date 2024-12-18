package interpreter.ast;

import interpreter.Token;

public record Reference(Token token) implements AbstractSyntaxTree {
    @Override
    public AbstractSyntaxTree copy() {
        return new Reference(token);
    }
}
