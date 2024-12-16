package interpreter.ast;

import interpreter.Token;

public class Constant extends Variable {

    private boolean initialized = false;

    public Constant(String name, Token type, String scope) {
        super(name, type, scope);
    }

    private Constant(String name, DataType type, String scope) {
        super(name, type, scope);
    }

//    @Override
//    public void setValue(double value) {
//        if (initialized) {
//            throw new IllegalStateException("Поле уже инициализировано");
//        }
//
//        super.setValue(value);
//        initialized = true;
//


    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public AbstractSyntaxTree copy() {
        return new Constant(name(), type(), scope());
    }
}
