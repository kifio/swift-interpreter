package interpreter.ast;

import interpreter.Function;
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
        } else if (ast instanceof FunctionCall function) {
            visitFunction(function);
        }
    }

    void visitAssign(Assign assign, String scope) {
        Variable variable = visitLvalue(assign.left());
        ExpressionResult result = visitRvalue(assign.right(), variable.type(), variable.scope());

        if (variable.type() == null) {
            variable.setType(result.type);
        }

        Interpreter.SCOPES.get(scope).put(variable.name(), result.value);
        System.out.println(result.value);
    }

    void visitFunction(FunctionCall functionCall) {
        Function function = Interpreter.FUNCTIONS.get(functionCall.name());

        for (Token name: functionCall.args().keySet()) {
            Constant c = (Constant) function.args().get(name.value()).copy();
            c.setValue(visitNumber(new Number(functionCall.args().get(name)), c.type()).value);
            Interpreter.SCOPES.get(function.name()).put(name.value(), c);
        }

        for (AbstractSyntaxTree statement : functionCall.statementList()) {
            visitAST(statement);
        }
    }

    Variable visitLvalue(AbstractSyntaxTree lvalue) {
        if (lvalue instanceof Variable) {
            return (Variable) lvalue;
        } else {
            throw new IllegalStateException("Выражение слева не является валидным идентификатором");
        }
    }

    ExpressionResult visitRvalue(AbstractSyntaxTree ast, DataType valueType, String scope) {
        return switch (ast) {
            case BinaryOperation op -> visitBinaryOp(op, valueType, scope);
            case UnaryOperation op -> visitUnaryOp(op, valueType, scope);
            case Number n -> visitNumber(n, valueType);
            case Variable v -> visitVariable(v, valueType, scope);
            default -> throw new IllegalStateException(ast + " не является rValue");
        };
    }

    ExpressionResult visitBinaryOp(BinaryOperation op, DataType valueType, String scope) {
        return visitRvalue(op.left(), valueType, scope).apply(visitRvalue(op.right(), valueType, scope), op.opToken());
    }

    ExpressionResult visitUnaryOp(UnaryOperation op, DataType valueType, String scope) {
        if (op.opToken() == Token.PLUS) {
            return visitRvalue(op.right(), valueType, scope);
        } else if (op.opToken() == Token.MINUS) {
            ExpressionResult result = visitRvalue(op.right(), valueType, scope);
            return new ExpressionResult(
                    result.type,
                    -result.value
            );
        } else {
            throw new IllegalStateException("Неизвестная унарная операция при вычислении: " + op.opToken());
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

    ExpressionResult visitVariable(Variable variable, DataType valueType, String scope) {
        if (scope.equals(Interpreter.GLOBAL_SCOPE) || scope.equals(variable.scope())) {
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

        throw new NumberFormatException("Переменная из области видимости " + variable.scope() + " недоступна в " + scope);
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
