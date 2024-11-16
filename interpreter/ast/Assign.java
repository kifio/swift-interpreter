package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

public record Assign(
        AbstractSyntaxTree left,
        Token opToken,
        AbstractSyntaxTree right
) implements AbstractSyntaxTree {

    @Override
    public double calculate() {
        if (left instanceof Identificator id) {
            double rValue = right.calculate();
            Token valueType = id.type();

            if (valueType != null && valueType.type() == Token.Type.INT_TYPE) {
                rValue = (int) rValue;
            }

            Interpreter.SYMBOL_TABLE.put(id.value().value(), rValue);
            System.out.println(id.value() + " = " + right.calculate());
            return 0;
        } else {
            throw new IllegalStateException("Выражение слева не является валидной переменной");
        }

    }
}
