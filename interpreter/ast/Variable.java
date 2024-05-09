package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

public record Variable(Token type, Token value) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        if (Interpreter.SYMBOL_TABLE.containsKey(this.value.value())) {
            return Interpreter.SYMBOL_TABLE.get(this.value.value());
        } else {
            throw new IllegalStateException();
        }
    }

}
