package interpreter.ast;

import interpreter.Token;

public class Variable implements AbstractSyntaxTree {

    private static DataType mapType(Token type) {
        if (type == Token.INTEGER) {
            return DataType.INTEGER;
        } else if (type == Token.DOUBLE) {
            return DataType.DOUBLE;
        } else {
            return null;
        }
    }

    private DataType type;
//    private Double value;
    private String name;
    private String scope;

    public Variable(String name, Token type, String scope) {
        this(name, mapType(type), scope);
    }

    protected Variable(String name, DataType type, String scope) {
        this.type = type;
        this.scope = scope;
        this.name = name;
    }

    public DataType type() {
        return type;
    }

    public String name() {
        return name;
    }

//    public Double value() {
//        return value;
//    }

    public String scope() {
        return scope;
    }

//    public void setValue(double value) {
//        this.value = value;
//    }

    public void setType(DataType type) {
        this.type = type;
    }

    @Override
    public AbstractSyntaxTree copy() {
        return new Variable(name, type, scope);
    }
}
