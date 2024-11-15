package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

import static interpreter.Interpreter.VARIABLES;

public record Variable(Token type, Token valueType, Token value) implements AbstractSyntaxTree {

    @Override
    public double calculate() {
        return Interpreter.SYMBOL_TABLE.getOrDefault(this.value.value(), 0.0);
    }

}
