package interpreter.ast;

import java.util.HashMap;
import java.util.UUID;

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
            visitFunction(function, scope);
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

    void visitFunction(FunctionCall functionCall, String outerScope) {
        String scope = functionCall.name() + "_" + UUID.randomUUID();
        Function function = Interpreter.FUNCTIONS.get(functionCall.name());

        Interpreter.SCOPES.put(scope, new HashMap<>());

        for (Token name: functionCall.args().keySet()) {
            Identifier identifier = new Identifier(function.args().get(name.value()));
            Token value = functionCall.args().get(name);

            ExpressionResult arg;

            if (value.type() == Token.Type.ID) {
                arg = visitVariable(new Reference(value), identifier.getDataType(), outerScope);
            } else {
                arg = visitNumber(new Number(value), identifier.getDataType());
            }

            identifier.setValue(arg.value);
            Interpreter.SCOPES.get(scope).put(identifier.getName(), identifier);
        }

        System.out.println(scope);

        for (AbstractSyntaxTree statement : function.getStatementList()) {
            visitAST(statement, scope);
        }
    }

    Identifier visitLvalue(AbstractSyntaxTree lvalue, String scope) {
        Variable variable = (Variable) lvalue;
        if (Interpreter.SCOPES.get(Interpreter.GLOBAL_SCOPE).containsKey(variable.name().value())) {
            return Interpreter.SCOPES.get(Interpreter.GLOBAL_SCOPE).get(variable.name().value());
        } else if (Interpreter.SCOPES.get(scope).containsKey(variable.name().value())) {
            return Interpreter.SCOPES.get(scope).get(variable.name().value());
        } else {
            Identifier identifier = new Identifier(variable.name(), variable.valueType(), variable.type() == Token.LET);
            Interpreter.SCOPES.get(scope).put(variable.name().value(), identifier);
            return identifier;
        }
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
            return new ExpressionResult( result.type, -result.value);
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
                        Double.parseDouble(number.token().value())
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
        Identifier identifier;

        if (Interpreter.SCOPES.get(scope).containsKey(reference.token().value())) {
            identifier = Interpreter.SCOPES.get(scope).get(reference.token().value());
        } else if (Interpreter.SCOPES.get(Interpreter.GLOBAL_SCOPE).containsKey(reference.token().value())) {
            identifier = Interpreter.SCOPES.get(Interpreter.GLOBAL_SCOPE).get(reference.token().value());
        } else {
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

        private Identifier.DataType type;
        private double value;

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
                throw new IllegalStateException(String.format("Неизвестная операция при вычислении %s", operation.value()));
            }

            return new ExpressionResult(determineType(result.type), resultValue);
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