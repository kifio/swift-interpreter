package interpreter.ast;

import interpreter.Token;

public class Variable implements AbstractSyntaxTree {

    private DataType type;
    private Double value;

    public Variable(Token type) {
        if (type == Token.INTEGER) {
            this.type = DataType.INTEGER;
        } else if (type == Token.DOUBLE) {
            this.type = DataType.DOUBLE;
        }
    }

    public DataType type() {
        return type;
    }

    public Double value() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setType(DataType type) {
        this.type = type;
    }
}
