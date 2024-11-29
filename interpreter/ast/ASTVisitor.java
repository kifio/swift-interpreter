package interpreter.ast;

import interpreter.Token;

public class ASTVisitor {

    private static class ExpressionResult {

        Identificator.Type type;

        double value;

        ExpressionResult(Identificator.Type type, double value) {
            this.type = type;
            this.value = value;
        }

        ExpressionResult apply(ExpressionResult result, Token operation) {
            double resultValue;

            if (operation == Token.PLUS) {
                resultValue = value + result.value;
            } else if (operation == Token.MINUS) {
                resultValue = value - result.value;
            } else if (operation == Token.MUL) {
                resultValue = value * result.value;
            } else if (operation == Token.DIV) {
                resultValue = value / result.value;
            } else {
                throw new IllegalStateException(String.format("Неизвестная операция при вычислении", operation));
            }

             return new ExpressionResult(
                     determineType(result.type),
                     resultValue
             );
        }

        ExpressionResult minus(ExpressionResult result) {
            return new ExpressionResult(
                    determineType(result.type),
                    value - result.value
            );
        }

        ExpressionResult mul(ExpressionResult result) {
            return new ExpressionResult(
                    determineType(result.type),
                    value * result.value
            );
        }

        ExpressionResult div(ExpressionResult result) {
            return new ExpressionResult(
                    determineType(result.type),
                    value / result.value
            );
        }

        private Identificator.Type determineType(Identificator.Type type) {
            if (type == Identificator.Type.DOUBLE || this.type == Identificator.Type.DOUBLE) {
                return Identificator.Type.DOUBLE;
            } else  {
                return Identificator.Type.INTEGER;
            }
        }
    }

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
        ExpressionResult result = visitRvalue(assign.right(), id.type());

        if (id.type() == null) {
            id.setType(result.type);
        }

        id.setValue(result.value);
        System.out.println(result.value);
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

    ExpressionResult visitRvalue(AbstractSyntaxTree ast, Identificator.Type valueType) {
        return switch (ast) {
            case BinaryOperation op -> visitBinaryOp(op, valueType);
            case UnaryOperation op -> visitUnaryOp(op, valueType);
            case Number n -> visitNumber(n, valueType);
            case Identificator v -> visitId(v, valueType);
            default -> throw new IllegalStateException(ast + " не является rValue");
        };
    }

    ExpressionResult visitBinaryOp(BinaryOperation op, Identificator.Type valueType) {
        return visitRvalue(op.left(), valueType).apply(visitRvalue(op.right(), valueType), op.opToken());
    }

    ExpressionResult visitUnaryOp(UnaryOperation op, Identificator.Type valueType) {
        if (op.opToken() == Token.PLUS) {
            return visitRvalue(op.right(), valueType);
        } else if (op.opToken() == Token.MINUS) {
            ExpressionResult result = visitRvalue(op.right(), valueType);
            return new ExpressionResult(
                    result.type,
                    -result.value
            );
        } else {
            throw new IllegalStateException(String.format("Неизвестная унарная операция при вычислении", op.opToken()));
        }
    }

    ExpressionResult visitNumber(Number number, Identificator.Type valueType) {
        if (valueType == null) {
            try {
                return new ExpressionResult(
                        Identificator.Type.INTEGER,
                        Integer.parseInt(number.token().value())
                );
            } catch (NumberFormatException e) {
                return new ExpressionResult(
                        Identificator.Type.DOUBLE,
                        Integer.parseInt(number.token().value())
                );
            }
        } else if (valueType == Identificator.Type.INTEGER) {
            return new ExpressionResult(
                    Identificator.Type.INTEGER,
                    Integer.parseInt(number.token().value())
            );
        } else /*if (valueType == Identificator.Type.DOUBLE)*/ {
            return new ExpressionResult(
                    Identificator.Type.DOUBLE,
                    Double.parseDouble(number.token().value())
            );
        }
    }

    ExpressionResult visitId(Identificator id, Identificator.Type valueType) {
        if (valueType == Identificator.Type.INTEGER) {
            if (id.type() == Identificator.Type.INTEGER) {
                return new ExpressionResult(
                        Identificator.Type.INTEGER,
                        id.value()
                );
            } else  {
                throw new NumberFormatException("Нельзя записать " + id.type() + " в " + valueType);
            }
        } else {
            return new ExpressionResult(
                    Identificator.Type.DOUBLE,
                    id.value()
            );
        }
    }
}
