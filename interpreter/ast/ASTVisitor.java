package interpreter.ast;

import interpreter.Interpreter;
import interpreter.Token;

public class ASTVisitor {

    public void visitAST(AbstractSyntaxTree ast) {
        if (ast instanceof Compound compound) {
            for (AbstractSyntaxTree statement : compound.statementList()) {
                visitAST(statement);
            }
        } else if (ast instanceof Assign assign) {
            visitAssign(assign);
        }
    }

    void visitAssign(Assign assign) {
        Identificator id = visitLvalue(assign.left());
        double value = visitRvalue(assign.right(), id.type());
        id.setValue(value);
    }

    // Возвращает тип идентификатора (INTEGER, DOUBLE).
    // В случае, если это инициализированная константа выбрасывает ошибку.
    Identificator visitLvalue(AbstractSyntaxTree id) {
        if (id instanceof Variable) {
            return (Variable) id;
        } else if (id instanceof Constant c) {
            if (c.value() != null) {
                throw new IllegalStateException("Поле уже инициализировано");
            }

            return c;
        } else {
            throw new IllegalStateException("Выражение слева не является валидным идентификатором");
        }
    }

    double visitRvalue(AbstractSyntaxTree ast, Identificator.Type valueType) {
        return switch (ast) {
            case BinaryOperation op -> visitBinaryOp(op, valueType);
            case UnaryOperation op -> visitUnaryOp(op, valueType);
            case Number n -> visitNumber(n, valueType);
            default -> throw new IllegalStateException(ast + " не является rValue");
        };
    }

    double visitBinaryOp(BinaryOperation op, Identificator.Type valueType) {
        return switch (op.opToken().type()) {
            case PLUS -> visitRvalue(op.left(), valueType) + visitRvalue(op.right(), valueType);
            case MINUS -> visitRvalue(op.left(), valueType) - visitRvalue(op.right(), valueType);
            case MUL -> visitRvalue(op.left(), valueType) * visitRvalue(op.right(), valueType);
            case DIV -> visitRvalue(op.left(), valueType) / visitRvalue(op.right(), valueType);
            default ->
                    throw new IllegalStateException(String.format("Неизвестная операция при вычислении", op.opToken().type()));
        };
    }

    double visitUnaryOp(UnaryOperation op, Identificator.Type valueType) {
        if (op.opToken() == Token.PLUS) {
            return visitRvalue(op.right(), valueType);
        } else if (op.opToken() == Token.MINUS) {
            return -visitRvalue(op.right(), valueType);
        } else {
            throw new IllegalStateException(String.format("Неизвестная унарная операция при вычислении", op.opToken()));
        }
    }

    double visitNumber(Number number, Identificator.Type valueType) {
        if (valueType == Identificator.Type.INTEGER) {
            return Integer.parseInt(number.token().value());
        } else {
            return Double.parseDouble(number.token().value());
        }
    }
}
