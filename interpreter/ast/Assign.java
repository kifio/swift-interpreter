package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

public record Assign(
    AbstractSyntaxTree left,
    Token opToken,
    AbstractSyntaxTree right
    ) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        if (left instanceof Variable) {
            Interpreter.SYMBOL_TABLE.put(((Variable) left).value().value(), right.calculate());
            System.out.println(((Variable) left).value() + " = " + right.calculate());
            return 0;
        } else {
            throw new IllegalStateException("Выражение слева не является валидной переменной");
        }

    }
}
