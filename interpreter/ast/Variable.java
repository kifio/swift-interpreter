package interpreter.ast;

import interpreter.Token;

public class Variable implements AbstractSyntaxTree {

    private DataType type;
    private Double value;
    private String scope;

    public Variable(Token type, String scope) {
        if (type == Token.INTEGER) {
            this.type = DataType.INTEGER;
        } else if (type == Token.DOUBLE) {
            this.type = DataType.DOUBLE;
        }

        this.scope = scope;
    }

    public DataType type() {
        return type;
    }

    public Double value() {
        return value;
    }

    public String scope() {
        return scope;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setType(DataType type) {
        this.type = type;
    }
}
