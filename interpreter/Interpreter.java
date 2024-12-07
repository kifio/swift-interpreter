package interpreter;
/*
 * program : compound_statement
 * compound_statement : statement_list
 * statement_list : statement | statement (SEMI | NEW_LINE) statement_list
 * statement : compound_statement | assignment_statement | empty | function
 * assignment_statement : ((LET | VAR) | empty) variable (COLON (INT | DOUBLE) | empty) ASSIGN expr
 * empty :
 * expr: term ((PLUS | MINUS) term)*
 * term: factor ((MUL | DIV) factor)*
 * factor : PLUS factor
 *        | MINUS factor
 *        | INTEGER
 *        | LPAREN expr RPAREN
 *        | variable
 * variable: ID
 * function: FUNC name LPAREN (variable COLON (INT | DOUBLE) | empty) RPAREN OPENING_CURLY_BRACE compound_statement CLOSING_CURLY_BRACE
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
        return compoundStatement();
    }

    private AbstractSyntaxTree compoundStatement() {
        return new Compound(statementList());
    }

    private List<AbstractSyntaxTree> statementList() {
        var statements = new ArrayList<AbstractSyntaxTree>();

        while (currentToken == Token.NEW_LINE || currentToken == Token.SEMI) {
            currentToken = lexer.readNextToken();

            if (currentToken == Token.NEW_LINE || currentToken == Token.SEMI) {
                continue;
            } else if (currentToken == Token.EOF) {
                break;
            }

            statements.add(statement());
        }

        if (currentToken != Token.EOF) {
            throw new IllegalStateException("Некорректное выражение");
        }

        return statements;
    }

    private AbstractSyntaxTree statement() {
        if (currentToken == Token.FUNC) {
            return functionDeclaration();
        } else {
            return assignStatement(variableDeclaration());
        }
    }

    private AbstractSyntaxTree variableDeclaration() {
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
                variable = new Constant(valueType);
            } else /* if (idType == Token.Type.VAR) */ {
                variable = new Variable(valueType);
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

    private AbstractSyntaxTree functionDeclaration() {
        currentToken = lexer.readNextToken();

        if (FUNCTIONS.containsKey(currentToken.value())) {
            throw new IllegalStateException("Идентификатор " + currentToken.value() + " уже объявлен.");
        }

        currentToken = lexer.readNextToken();

        if (currentToken != Token.LPAREN) {
            throw new IllegalStateException("Ожидался список аругментов функции");
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
    
            args.put(name.value(), new Constant(type));

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

        do  {
            currentToken = lexer.readNextToken();
        } while (currentToken != Token.CLOSING_CURLY_BRACE);

        currentToken = lexer.readNextToken();

        Function f = new Function(args);
        FUNCTIONS.put(currentToken.value(), f);
        return f;
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