package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

public class Constant extends Identificator {

    public Constant(Token type, Token value) {
        super(type, value);
    }

    @Override
    public double calculate() {
        return Interpreter.SYMBOL_TABLE.getOrDefault(this.value().value(), 0.0);
    }

}
