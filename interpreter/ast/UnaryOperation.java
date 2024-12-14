package interpreter.ast;

import interpreter.Token;

public record UnaryOperation(Token opToken, AbstractSyntaxTree right) implements AbstractSyntaxTree {
    @Override
    public AbstractSyntaxTree copy() {
        return new UnaryOperation(opToken, right.copy());
    }
}
