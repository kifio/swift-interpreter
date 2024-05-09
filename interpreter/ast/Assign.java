package interpreter.ast;

import interpreter.Token;

public record Assign(
    AbstractSyntaxTree left,
    Token opToken,
    AbstractSyntaxTree right
    ) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculate'");
    }
}
