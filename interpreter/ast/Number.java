package interpreter.ast;

import interpreter.Token;

public record Number(Token token) implements AbstractSyntaxTree {
    @Override
    public double calculate() {
        return Double.parseDouble(token.value());
    }
}
