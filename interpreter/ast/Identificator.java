package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

public abstract class Identificator<T> implements AbstractSyntaxTree {

    enum Type {
        INTEGER, DOUBLE
    }

    private Type type;
    private T value;

    protected Identificator(Type type, T value) {
        this.type = type;
        this.value = value;
    }

    public Type type() {
        return type;
    }

    public T value() {
        return value;
    }

    public void update(T value) {
        this.value = value;
    }

    private void validate() {
        if (type == Type.INTEGER && value instanceof Integer) {
            return;
        } else if (type == Type.DOUBLE && value instanceof Double) {
            return;
        } else {
            throw new IllegalStateException("В переменную типа " + type.name() + " нельзя записать значение " + value);
        }
    }
}
