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
        } else if (type == Token.DOUBLE) {
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

    public void setType(Identificator.Type type) {
        this.type = type;
    }
}
