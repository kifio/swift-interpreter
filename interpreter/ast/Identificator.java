package interpreter.ast;

import interpreter.Token;

public class Identificator implements AbstractSyntaxTree {

    public enum Type {
        INTEGER, DOUBLE
    }

    private Type type;
    private Double value;

    protected Identificator(Token type) {
        if (type == Token.INTEGER) {
            this.type = Type.INTEGER;
        } else {
            this.type = Type.DOUBLE;
        }
    }

    public Type type() {
        return type;
    }

    public Double value() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

//    private void validate() {
//        if (type == Type.INTEGER && value instanceof Integer) {
//            return;
//        } else if (type == Type.DOUBLE && value instanceof Double) {
//            return;
//        } else {
//            throw new IllegalStateException("В переменную типа " + type.name() + " нельзя записать значение " + value);
//        }
//    }
}
