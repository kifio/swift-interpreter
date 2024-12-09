package interpreter;
/*
 * program : compound_statement
 * compound_statement : statement_list
 * statement_list : statement | statement (SEMI | NEW_LINE) statement_list
 * statement : compound_statement | assignment_statement | empty | function
 * assignment_statement : variable ASSIGN expr
 * empty :
 * expr: term ((PLUS | MINUS) term)*
 * term: factor ((MUL | DIV) factor)*
 * factor : PLUS factor
 *        | MINUS factor
 *        | INTEGER
 *        | LPAREN expr RPAREN
 *        | variable
 * variable: ((LET | VAR) | empty) variable (COLON (INT | DOUBLE) | empty)
 * function: FUNC ID LPAREN formal_parameter_list RPAREN OPENING_CURLY_BRACE compound_statement CLOSING_CURLY_BRACE
 * formal_parameter_list: formal_parameter | formal_parameter COMMA formal_parameter_list
 * formal_parameter: ID COLON (INT | DOUBLE) | empty
 * function_call: ID(function_call_parameter_list)
 * function_call_parameter_list: function_call_parameter | function_call_parameter COMMA function_call_parameter_list
 * function_call_parameter: ID COLON expr | empty
 */

import interpreter.ast.Number;
import interpreter.ast.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interpreter {

    private static final HashMap<String, Variable> RUNTIME_MEMORY = new HashMap<>();
    private static final HashMap<String, Function> FUNCTIONS = new HashMap<>();

    public static final String GLOBAL_SCOPE = "GLOBAL_SCOPE";

    private final Lexer lexer = new Lexer();
    private Token currentToken;

    public static void main(String[] args) {
        try {
            Path contents = FileSystems
                    .getDefault()
                    .getPath("")
                    .resolve("interpreter")
                    .resolve("Contents.swift");

            StringBuilder sb = new StringBuilder();
            List<String> lines = Files.readAllLines(contents);

            for (String line : lines) {
                sb.append(line);
                sb.append('\n');
            }

            new ASTVisitor().visitAST(new Interpreter().interpret(sb.toString().trim()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AbstractSyntaxTree interpret(String expression) {
        lexer.initialize(expression);
        currentToken = lexer.readNextToken();
        return program();
    }

    private AbstractSyntaxTree program() {
        return compoundStatement(GLOBAL_SCOPE);
    }

    private AbstractSyntaxTree compoundStatement(String scope) {
        return new Compound(statementList(scope));
    }

    private List<AbstractSyntaxTree> statementList(String scope) {
        var statements = new ArrayList<AbstractSyntaxTree>();

        while (currentToken == Token.NEW_LINE || currentToken == Token.SEMI) {
            currentToken = lexer.readNextToken();

            if (currentToken == Token.NEW_LINE || currentToken == Token.SEMI) {
                continue;
            } else if (currentToken == Token.EOF || currentToken == Token.CLOSING_CURLY_BRACE) {
                break;
            }

            if (currentToken == Token.FUNC) {
                functionDeclaration();
            } else if (FUNCTIONS.containsKey(currentToken.value()) ) {
                statements.add(functionCall(FUNCTIONS.get(currentToken.value())));
            } else {
                statements.add(assignStatement(variableDeclaration(scope)));
            }
        }

        if (currentToken == Token.EOF || currentToken == Token.CLOSING_CURLY_BRACE) {
            return statements;
        }

        throw new IllegalStateException("Некорректное выражение");
    }

    private AbstractSyntaxTree variableDeclaration(String scope) {
        if (currentToken == Token.LET || currentToken == Token.VAR) {
            Token variableType = currentToken;

            currentToken = lexer.readNextToken();

            if (currentToken.type() != Token.Type.ID) {
                throw new IllegalStateException(
                        "Некорректный токен: " + currentToken.type() + " Ожидалось имя переменной."
                );
            }

            Token name = currentToken;

            if (RUNTIME_MEMORY.containsKey(name.value())) {
                throw new IllegalStateException(
                        "Идентификатор " + currentToken.value() + " уже объявлен."
                );
            }

            Token valueType = null;
            currentToken = lexer.readNextToken();

            if (currentToken == Token.COLON) {
                currentToken = lexer.readNextToken();

                if (currentToken == Token.INTEGER || currentToken == Token.DOUBLE) {
                    valueType = currentToken;
                } else {
                    throw new IllegalStateException(
                            "Некорректный токен: " + currentToken.type() + " Ожидался тип переменной.");
                }

                currentToken = lexer.readNextToken();
            }

            Variable variable;

            if (variableType == Token.LET) {
                variable = new Constant(valueType, scope);
            } else /* if (idType == Token.Type.VAR) */ {
                variable = new Variable(valueType, scope);
            }

            RUNTIME_MEMORY.put(name.value(), variable);
            return variable;
        }

        AbstractSyntaxTree variable = RUNTIME_MEMORY.get(currentToken.value());

        if (variable != null) {
            currentToken = lexer.readNextToken();
            return variable;
        }

        throw new IllegalStateException("Некорректный токен: " + currentToken.value());
    }

    private AbstractSyntaxTree assignStatement(AbstractSyntaxTree variable) {

        if (currentToken == Token.NEW_LINE) {
            return variable;
        }

        if (currentToken != Token.ASSIGN) {
            throw new IllegalStateException("Некорректный токен: " + currentToken + " Ожидался оператор присваивания.");
        }

        var assignToken = currentToken;
        currentToken = lexer.readNextToken();

        return new Assign(variable, assignToken, expr());
    }

    private void functionDeclaration() {
        currentToken = lexer.readNextToken();

        String functionName = currentToken.value();

        currentToken = lexer.readNextToken();

        if (currentToken != Token.LPAREN) {
            throw new IllegalStateException("Ожидался список аргументов функции");
        }

        HashMap<String, Constant> args = new HashMap<>();

        currentToken = lexer.readNextToken();

        while (currentToken != Token.RPAREN)  {

            Token name = currentToken;
            Token colon = lexer.readNextToken();
            Token type = lexer.readNextToken();

            if (name.type() != Token.Type.ID || colon != Token.COLON || (type != Token.INTEGER && type != Token.DOUBLE)) {
                throw new IllegalStateException("Ожидался список аргументов функции вида имя: тип, имя: тип....");
            }
    
            args.put(name.value(), new Constant(type, functionName));

            currentToken = lexer.readNextToken();

            if (currentToken != Token.COMMA) {
                if (currentToken == Token.RPAREN) {
                    break;
                } else {
                    throw new IllegalStateException("Аргументы должны быть разделены запятыми");
                }
            }

            currentToken = lexer.readNextToken();
        }

        currentToken = lexer.readNextToken();

        if (currentToken != Token.OPENING_CURLY_BRACE) {
            throw new IllegalStateException("Тело функции должно быть заключено в фигурные скобки");
        }

        currentToken = lexer.readNextToken();

        List<AbstractSyntaxTree> functionBody = statementList(functionName);

        currentToken = lexer.readNextToken();

        Function f = new Function(functionName, args, functionBody);

        if (FUNCTIONS.containsKey(functionName)) {
            throw new IllegalStateException("Функция " + functionName + "с такими параметрами уже объявлена.");
        }

        FUNCTIONS.put(functionName, f);
    }

    private AbstractSyntaxTree functionCall(Function function) {
        // TODO: Прочитать список аргументов в вызове функции
//        for (String arg: function.args().keySet()) {
//            Variable v = RUNTIME_MEMORY.get(arg);
//            if (v.scope().equals(function.name())) {
//                v.setValue(a);
//            }
//        }
    }

    private AbstractSyntaxTree expr() {
        var left = term();

        while (currentToken.isSumOrSub()) {
            var op = currentToken;
            currentToken = lexer.readNextToken();
            left = new BinaryOperation(left, op, term());
        }

        return left;
    }

    private AbstractSyntaxTree term() {
        var left = factor();

        while (currentToken.isMulOrDiv()) {
            var op = currentToken;
            currentToken = lexer.readNextToken();
            left = new BinaryOperation(left, op, factor());
        }

        return left;
    }

    private AbstractSyntaxTree factor() {
        if (currentToken.isSumOrSub()) {
            var op = currentToken;
            currentToken = lexer.readNextToken();
            return new UnaryOperation(op, factor());
        } else if (currentToken == Token.LPAREN) {
            currentToken = lexer.readNextToken();
            var node = expr();
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.NUMBER) {
            var node = new Number(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.ID && RUNTIME_MEMORY.containsKey(currentToken.value())) {
            var node = RUNTIME_MEMORY.get(currentToken.value());
            currentToken = lexer.readNextToken();
            return node;
        }

        throw new IllegalStateException("Некорректный токен: " + currentToken.value());
    }
}