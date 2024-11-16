package interpreter.ast;

import interpreter.Token;

public record BinaryOperation(
        AbstractSyntaxTree left,
        Token opToken,
        AbstractSyntaxTree right
) implements AbstractSyntaxTree {
    @Override
    public double calculate() {
        return switch (opToken.type()) {
            case PLUS -> left.calculate() + right.calculate();
            case MINUS -> left.calculate() - right.calculate();
            case MUL -> left.calculate() * right.calculate();
            case DIV -> left.calculate() / right.calculate();
            default ->
                    throw new IllegalStateException(String.format("Неизвестная операция при вычислении", opToken.type()));
        };
    }
}
