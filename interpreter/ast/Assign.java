package interpreter.ast;

import interpreter.Token;

public record Assign(AbstractSyntaxTree left, Token opToken, AbstractSyntaxTree right) implements AbstractSyntaxTree {
    @Override
    public AbstractSyntaxTree copy() {
        return new Assign(left.copy(), opToken, right.copy());
    }
}
