package interpreter.ast;

import interpreter.Token;

public class Constant extends Identificator {

    public Constant(Token type) {
        super(type);
    }

//    @Override
//    public void calculate() {
//        this.update();
//        return Interpreter.SYMBOL_TABLE.getOrDefault(this.value().value(), 0.0);
//    }
}
