package calc7;

public record BinaryOperation(
        AbstractSyntaxTree left,
        Token opToken,
        AbstractSyntaxTree right
) implements AbstractSyntaxTree {
    @Override
    public int calculate() {
        switch (opToken.type()) {
            case PLUS:
                return left.calculate() + right.calculate();
            case MINUS:
                return left.calculate() - right.calculate();
            case MUL:
                return left.calculate() * right.calculate();
            case DIV:
                return left.calculate() / right.calculate();
            default:
                throw new IllegalStateException(String.format("Неизвестная операция при вычислении", opToken.type()));
        }
    }
}
