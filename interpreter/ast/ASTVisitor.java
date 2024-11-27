package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

import static interpreter.Token.Type;

public class ASTVisitor {

    void visitAssign(Assign assign) {
        if (assign.left() instanceof Identificator id) {
            double value = visitRvalue(assign.right());
            visitLvalue();
//            Token valueType = id.type();
//
//            if (valueType != null && valueType.type() == Token.Type.INT_TYPE) {
//                rValue = (int) rValue;
//            }

            Interpreter.SYMBOL_TABLE.put(id.value().value(), rValue);
        } else {
            throw new IllegalStateException("Выражение слева не является валидной переменной");
        }
    }

    void visitLvalue(AbstractSyntaxTree ast) {

    }

    double visitRvalue(AbstractSyntaxTree ast) {
        switch (ast) {
            case BinaryOperation op:
                visitBinaryOp(op);
                break;
            case UnaryOperation op:
                visitUnaryOp(op);
                break;
            case Number n:
                visitNumber(n);
                break;
            default:
                throw new IllegalStateException(ast + " не является rValue");
        }
    }

    double visitBinaryOp(BinaryOperation op) {
        return switch (op.opToken().type()) {
            case PLUS -> visitRvalue(op.left()) + visitRvalue(op.right());
            case MINUS -> visitRvalue(op.left()) - visitRvalue(op.right());
            case MUL -> visitRvalue(op.left()) * visitRvalue(op.right());
            case DIV -> visitRvalue(op.left()) / visitRvalue(op.right());
            default ->
                    throw new IllegalStateException(String.format("Неизвестная операция при вычислении", op.opToken().type()));
        };
    }

    double visitUnaryOp(UnaryOperation op) {
        return switch (op.opToken().type()) {
            case PLUS -> visitRvalue(op.right());
            case MINUS -> -visitRvalue(op.right());
            default ->
                    throw new IllegalStateException(String.format("Неизвестная унарная операция при вычислении", opToken.type()));
        };
    }

    double visitNumber(Number number) {
        return Double.parseDouble(number.token().value());
    }

}
