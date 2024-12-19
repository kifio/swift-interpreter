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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongBiFunction;

public class Interpreter {

    public static final Map<String, Map<String, Identifier>> SCOPES = new HashMap<>();
    public static final Map<String, Function> FUNCTIONS = new HashMap<>();

    public static final String GLOBAL_SCOPE = "GLOBAL_SCOPE";

    private final Lexer lexer = new Lexer();
    private Token currentToken;

    public Interpreter() {
        SCOPES.put(GLOBAL_SCOPE, new HashMap<>());
    }

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

            new ASTVisitor().visitAST(new Interpreter().interpret(sb.toString().trim()), GLOBAL_SCOPE);
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
        return compoundStatement();
    }

    private AbstractSyntaxTree compoundStatement() {
        return new Compound(statementList(GLOBAL_SCOPE));
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
                statements.add(assignStatement(variableDeclaration(scope), scope));
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
            SCOPES.putIfAbsent(scope, new HashMap<>());

            if (SCOPES.get(scope).containsKey(name.value())) {
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

            SCOPES.get(scope).put(name.value(), new Identifier(name, valueType, variableType == Token.LET));

            return new Variable(name);
        }

        if (currentToken.type() != Token.Type.ID) {
            throw new IllegalStateException(
                    "Некорректный токен: " + currentToken.type() + " Ожидалось имя переменной."
            );
        }

        Token name = currentToken;
        
        if (SCOPES.get(scope).containsKey(name.value()) || SCOPES.get(GLOBAL_SCOPE).containsKey(name.value())) {
            currentToken = lexer.readNextToken();
            return new Variable(name);
        }

        throw new IllegalStateException("Некорректный токен: " + currentToken.value());
    }

    private AbstractSyntaxTree assignStatement(AbstractSyntaxTree variable, String scope) {

        if (currentToken == Token.NEW_LINE) {
            return variable;
        }

        if (currentToken != Token.ASSIGN) {
            throw new IllegalStateException("Некорректный токен: " + currentToken + " Ожидался оператор присваивания.");
        }

        var assignToken = currentToken;
        currentToken = lexer.readNextToken();

        return new Assign(variable, assignToken, expr(scope));
    }

    private void functionDeclaration() {
        currentToken = lexer.readNextToken();

        if (FUNCTIONS.containsKey(currentToken.value())) {
            throw new IllegalStateException("Функция " + currentToken + " уже объявлена.");
        }

        String functionName = currentToken.value();

        currentToken = lexer.readNextToken();

        if (currentToken != Token.LPAREN) {
            throw new IllegalStateException("Ожидался список аргументов функции");
        }

        HashMap<String, Identifier> args = new HashMap<>();
        SCOPES.putIfAbsent(functionName, new HashMap<>());

        currentToken = lexer.readNextToken();

        while (currentToken != Token.RPAREN)  {

            Token name = currentToken;
            Token colon = lexer.readNextToken();
            Token type = lexer.readNextToken();

            if (name.type() != Token.Type.ID || colon != Token.COLON || (type != Token.INTEGER && type != Token.DOUBLE)) {
                throw new IllegalStateException("Ожидался список аргументов функции вида имя: тип, имя: тип....");
            }

            Identifier arg = new Identifier(name, type, true);

            args.put(name.value(), arg);
            SCOPES.get(functionName).put(name.value(), arg);

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

        Function f = new Function(functionName, args);

        FUNCTIONS.put(functionName, f);

        f.setStatementList(statementList(functionName));

        currentToken = lexer.readNextToken();


    }

    private AbstractSyntaxTree functionCall(Function function) {
        currentToken = lexer.readNextToken();

        if (currentToken != Token.LPAREN) {
            throw new IllegalStateException("Ожидается список значений аргументов функции");
        }

        Map<Token, Token> args = new HashMap<>();

        while (currentToken != Token.RPAREN) {

            currentToken = lexer.readNextToken();

            if (currentToken == Token.RPAREN) {
                break;
            }

            Token name = currentToken;

            if (name.type() != Token.Type.ID) {
                throw new IllegalStateException("Ожидалось имя аргумента функции");
            }

            if (!function.args().containsKey(name.value())) {
                throw new IllegalStateException("Неверный аргумент " + name + " + для функции " + function.name());
            }

            currentToken = lexer.readNextToken();

            if (currentToken != Token.COLON) {
                throw new IllegalStateException("Неправильное форматирование списка аргуементов функции");
            }

            Token value = lexer.readNextToken();

            if (!(value.type() == Token.Type.NUMBER || value.type() == Token.Type.ID)) {
                throw new IllegalStateException("Ожидалось значение аргумента функции");
            }

            args.put(name, value);
            currentToken = lexer.readNextToken();
        }


        FunctionCall functionCall = new FunctionCall(
                function.name(),
                args

        );

        currentToken = lexer.readNextToken();


        return  functionCall;
    }

    private AbstractSyntaxTree expr(String scope) {
        var left = term(scope);

        while (currentToken.isSumOrSub()) {
            var op = currentToken;
            currentToken = lexer.readNextToken();
            left = new BinaryOperation(left, op, term(scope));
        }

        return left;
    }

    private AbstractSyntaxTree term(String scope) {
        var left = factor(scope);

        while (currentToken.isMulOrDiv()) {
            var op = currentToken;
            currentToken = lexer.readNextToken();
            left = new BinaryOperation(left, op, factor(scope));
        }

        return left;
    }

    private AbstractSyntaxTree factor(String scope) {
        if (currentToken.isSumOrSub()) {
            var op = currentToken;
            currentToken = lexer.readNextToken();
            return new UnaryOperation(op, factor(scope));
        } else if (currentToken == Token.LPAREN) {
            currentToken = lexer.readNextToken();
            var node = expr(scope);
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.NUMBER) {
            var node = new Number(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.ID) {
            var node = new Reference(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        }

        throw new IllegalStateException("Некорректный токен: " + currentToken.value());
    }

    public static Identifier find(String variable, String scope) {
        if (SCOPES.containsKey(scope) && SCOPES.get(scope).containsKey(variable)) {
            return SCOPES.get(scope).get(variable);
        } else {
            return SCOPES.get(GLOBAL_SCOPE).get(variable);
        }
    }
}