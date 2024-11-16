package interpreter.ast;

import interpreter.Token;

public record UnaryOperation(
        Token opToken,
        AbstractSyntaxTree right
) implements AbstractSyntaxTree {

    @Override
    public double calculate() {
        return switch (opToken.type()) {
            case PLUS -> right.calculate();
            case MINUS -> -right.calculate();
            default ->
                    throw new IllegalStateException(String.format("Неизвестная унарная операция при вычислении", opToken.type()));
        };
    }

}
