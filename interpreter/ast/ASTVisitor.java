package interpreter.ast;

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
        Variable variable = visitLvalue(assign.left());
        ExpressionResult result = visitRvalue(assign.right(), variable.type());

        if (variable.type() == null) {
            variable.setType(result.type);
        }

        variable.setValue(result.value);
        System.out.println(result.value);
    }

    // Возвращает тип идентификатора (INTEGER, DOUBLE).
    // В случае, если это инициализированная константа выбрасывает ошибку.
    Variable visitLvalue(AbstractSyntaxTree lvalue) {
        if (lvalue instanceof Variable) {
            return (Variable) lvalue;
        } else {
            throw new IllegalStateException("Выражение слева не является валидным идентификатором");
        }
    }

    ExpressionResult visitRvalue(AbstractSyntaxTree ast, DataType valueType) {
        return switch (ast) {
            case BinaryOperation op -> visitBinaryOp(op, valueType);
            case UnaryOperation op -> visitUnaryOp(op, valueType);
            case Number n -> visitNumber(n, valueType);
            case Variable v -> visitVariable(v, valueType);
            default -> throw new IllegalStateException(ast + " не является rValue");
        };
    }

    ExpressionResult visitBinaryOp(BinaryOperation op, DataType valueType) {
        return visitRvalue(op.left(), valueType).apply(visitRvalue(op.right(), valueType), op.opToken());
    }

    ExpressionResult visitUnaryOp(UnaryOperation op, DataType valueType) {
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

    ExpressionResult visitNumber(Number number, DataType valueType) {
        if (valueType == null) {
            try {
                return new ExpressionResult(
                        DataType.INTEGER,
                        Integer.parseInt(number.token().value())
                );
            } catch (NumberFormatException e) {
                return new ExpressionResult(
                        DataType.DOUBLE,
                        Integer.parseInt(number.token().value())
                );
            }
        } else if (valueType == DataType.INTEGER) {
            return new ExpressionResult(
                    DataType.INTEGER,
                    Integer.parseInt(number.token().value())
            );
        } else /*if (valueType == DataType.DOUBLE)*/ {
            return new ExpressionResult(
                    DataType.DOUBLE,
                    Double.parseDouble(number.token().value())
            );
        }
    }

    ExpressionResult visitVariable(Variable variable, DataType valueType) {
        if (valueType == DataType.INTEGER) {
            if (variable.type() == DataType.INTEGER) {
                return new ExpressionResult(
                        DataType.INTEGER,
                        variable.value()
                );
            } else {
                throw new NumberFormatException("Нельзя записать " + variable.type() + " в " + valueType);
            }
        } else {
            return new ExpressionResult(
                    DataType.DOUBLE,
                    variable.value()
            );
        }
    }

    private static class ExpressionResult {

        DataType type;

        double value;

        ExpressionResult(DataType type, double value) {
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

        private DataType determineType(DataType type) {
            if (type == DataType.DOUBLE || this.type == DataType.DOUBLE) {
                return DataType.DOUBLE;
            } else {
                return DataType.INTEGER;
            }
        }
    }
}
