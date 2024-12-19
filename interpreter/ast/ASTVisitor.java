package interpreter.ast;

import java.sql.Ref;
import java.util.Collection;
import java.util.Set;

import interpreter.Function;
import interpreter.Identifier;
import interpreter.Interpreter;
import interpreter.Token;

public class ASTVisitor {

    public void visitAST(AbstractSyntaxTree ast, String scope) {
        if (ast instanceof Compound compound) {
            for (AbstractSyntaxTree statement : compound.statementList()) {
                visitAST(statement, scope);
            }
        } else if (ast instanceof Assign assign) {
            visitAssign(assign, scope);
        } else if (ast instanceof FunctionCall function) {
            visitFunction(function);
        }
    }

    void visitAssign(Assign assign, String scope) {
        Identifier identifier = visitLvalue(assign.left(), scope);

        if (identifier.canBeAssigned()) {
            ExpressionResult result = visitRvalue(assign.right(), identifier.getDataType(), scope);

            if (identifier.getDataType() == null) {
                identifier.setDataType(result.type);
            }

            identifier.setValue(result.value);
            System.out.println(identifier);
        } else {
            throw new IllegalStateException("Значение константы не может быть измененено");
        }
    }

    void visitFunction(FunctionCall functionCall) {
        String scope = functionCall.name();
        Function function = Interpreter.FUNCTIONS.get(scope);

        Collection<Identifier> localVariables = Interpreter.SCOPES.get(scope).values();

        localVariables.forEach(Identifier::reset);

        for (Token name: functionCall.args().keySet()) {
            Identifier identifier = new Identifier(function.args().get(name.value()));
            Token value = functionCall.args().get(name);

            ExpressionResult arg;

            if (value.type() == Token.Type.ID) {
                arg = visitVariable(new Reference(value), identifier.getDataType(), scope);
            } else {
                arg = visitNumber(new Number(value), identifier.getDataType());
            }

            identifier.setValue(arg.value);
            Interpreter.SCOPES.get(scope).put(identifier.getName(), identifier);
        }

        for (AbstractSyntaxTree statement : function.getStatementList().stream().map(AbstractSyntaxTree::copy).toList()) {
            visitAST(statement, scope);
        }
    }

    Identifier visitLvalue(AbstractSyntaxTree lvalue, String scope) {
        Variable variable = (Variable) lvalue;
        return Interpreter.find(variable.name().value(), scope);
    }

    ExpressionResult visitRvalue(AbstractSyntaxTree ast, Identifier.DataType valueType, String scope) {
        return switch (ast) {
            case BinaryOperation op -> visitBinaryOp(op, valueType, scope);
            case UnaryOperation op -> visitUnaryOp(op, valueType, scope);
            case Number n -> visitNumber(n, valueType);
            case Reference v -> visitVariable(v, valueType, scope);
            default -> throw new IllegalStateException(ast + " не является rValue");
        };
    }

    ExpressionResult visitBinaryOp(BinaryOperation op, Identifier.DataType valueType, String scope) {
        return visitRvalue(op.left(), valueType, scope).apply(visitRvalue(op.right(), valueType, scope), op.opToken());
    }

    ExpressionResult visitUnaryOp(UnaryOperation op, Identifier.DataType valueType, String scope) {
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

    ExpressionResult visitNumber(Number number, Identifier.DataType valueType) {
        if (valueType == null) {
            try {
                return new ExpressionResult(
                        Identifier.DataType.INTEGER,
                        Integer.parseInt(number.token().value())
                );
            } catch (NumberFormatException e) {
                return new ExpressionResult(
                        Identifier.DataType.DOUBLE,
                        Integer.parseInt(number.token().value())
                );
            }
        } else if (valueType == Identifier.DataType.INTEGER) {
            return new ExpressionResult(
                    Identifier.DataType.INTEGER,
                    Integer.parseInt(number.token().value())
            );
        } else /*if (valueType == DataType.DOUBLE)*/ {
            return new ExpressionResult(
                    Identifier.DataType.DOUBLE,
                    Double.parseDouble(number.token().value())
            );
        }
    }

    ExpressionResult visitVariable(Reference reference, Identifier.DataType valueType, String scope) {
        Identifier identifier = Interpreter.find(reference.token().value(), scope);

        if (identifier == null) {
            throw new NumberFormatException("Переменная " + reference.token().value() + " недоступна в " + scope);
        }

        if (valueType != Identifier.DataType.INTEGER) {
            return new ExpressionResult(
                    Identifier.DataType.DOUBLE,
                    identifier.getValue()
            );
        } else if (identifier.getDataType() == Identifier.DataType.INTEGER) {
            return new ExpressionResult(
                    Identifier.DataType.INTEGER,
                    identifier.getValue()
            );
        } else {
            throw new NumberFormatException("Нельзя записать " + identifier.getDataType() + " в " + valueType);
        }
    }

    private static class ExpressionResult {

        Identifier.DataType type;

        double value;

        ExpressionResult(Identifier.DataType type, double value) {
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

        private Identifier.DataType determineType(Identifier.DataType type) {
            if (type == Identifier.DataType.DOUBLE || this.type == Identifier.DataType.DOUBLE) {
                return Identifier.DataType.DOUBLE;
            } else {
                return Identifier.DataType.INTEGER;
            }
        }
    }
}
