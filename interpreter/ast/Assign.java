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
        if (left instanceof Variable) {
            Variable variable = (Variable) left;
            if (variable.type().type() == Token.Type.LET && Interpreter.SYMBOL_TABLE.containsKey(variable.value().value())) {
                throw new IllegalStateException("Невозможно изменить значение let константы");
            }

            double rValue = right.calculate();
            Token valueType = variable.valueType();

            if (valueType != null && valueType.type() == Token.Type.INT_TYPE) {
                rValue = (int) rValue;
            }

            Interpreter.SYMBOL_TABLE.put(variable.value().value(), rValue);
            System.out.println(((Variable) left).value() + " = " + right.calculate());
            return 0;
        } else {
            throw new IllegalStateException("Выражение слева не является валидной переменной");
        }

    }
}
