package calc7;

record UnaryOperation(
        Token opToken,
        AbstractSyntaxTree right
) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        switch (opToken.type()) {
            case PLUS:
                return right.calculate();
            case MINUS:
                return -right.calculate();
            default:
                throw new IllegalStateException(String.format("Неизвестная унарная операция при вычислении", opToken.type()));
        }
    }

}
