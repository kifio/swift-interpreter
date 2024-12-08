package interpreter.ast;

import interpreter.Token;

public class Constant extends Variable {

    private boolean initialized = false;

    public Constant(Token type, String scope) {
        super(type, scope);
    }

    @Override
    public void setValue(double value) {
        if (initialized) {
            throw new IllegalStateException("Поле уже инициализировано");
        }

        super.setValue(value);
        initialized = true;
    }
}
