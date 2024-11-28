package interpreter;
/*
 * program : compound_statement
 * compound_statement : statement_list
 * statement_list : statement | statement (SEMI | NEW_LINE) statement_list
 * statement : compound_statement | assignment_statement | empty
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

//    public static List<Variable> VARIABLES = new ArrayList<>();
//    public static List<Constant> CONSTANTS = new ArrayList<>();

    private static HashMap<String, Identificator> SYMBOL_TABLE = new HashMap<>();

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

        while (currentToken.equals(Token.NEW_LINE) || currentToken.equals(Token.SEMI)) {
            currentToken = lexer.readNextToken();

            if (currentToken.equals(Token.NEW_LINE) || currentToken.equals(Token.SEMI)) {
                continue;
            }

            statements.add(statement());
        }

        if (currentToken.type() != Token.Type.EOF) {
            throw new IllegalStateException("Некорректное выражение");
        }

        return statements;
    }

    private AbstractSyntaxTree statement() {
        return assignStatement(variable());
    }

    private AbstractSyntaxTree variable() {
        if (currentToken == Token.LET || currentToken == Token.VAR) {
            Token idType = currentToken;

            currentToken = lexer.readNextToken();

            if (currentToken.type() != Token.Type.ID) {
                throw new IllegalStateException(
                        "Некорректный токен: " + currentToken.type() + " Ожидалось имя переменной."
                );
            }

            Token name = currentToken;

            if (idExist(name) || idExist(name)) {
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

            if (idType == Token.LET) {
                return SYMBOL_TABLE.put(name.value(), new Constant(valueType));
            } else /* if (idType == Token.Type.VAR) */ {
                return SYMBOL_TABLE.put(name.value(), new Variable(valueType));
            }
        }

        AbstractSyntaxTree id = idAST(currentToken);

        if (id != null) {
            currentToken = lexer.readNextToken();
            return id;
        }

        throw new IllegalStateException("Некорректный токен: " + currentToken.value());
    }

    private boolean idExist(Token t) {
        return SYMBOL_TABLE.containsKey(t.value());
    }

    private AbstractSyntaxTree idAST(Token t) {
        return SYMBOL_TABLE.get(t.value());
    }

    private AbstractSyntaxTree assignStatement(AbstractSyntaxTree variable) {

        if (currentToken == Token.NEW_LINE) {
            return variable;
        }

        if (currentToken.type() != Token.Type.ASSIGN) {
            throw new IllegalStateException(
                    "Некорректный токен: " + currentToken.type() + " Ожидался оператор присваивания.");
        }

        var assignToken = currentToken;
        currentToken = lexer.readNextToken();

        return new Assign(variable, assignToken, expr());
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
        } else if (currentToken == Token.INTEGER) {
            var node = new Number(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken == Token.DOUBLE) {
            var node = new Number(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken == Token.LPAREN) {
            currentToken = lexer.readNextToken();
            var node = expr();
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.ID) {
            var node = idAST(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        }

        throw new IllegalStateException(
                String.format("Не удалось получить значение оператора. Обрабатываемый токен: %s", currentToken.type()));
    }
}