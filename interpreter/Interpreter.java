package interpreter;
/*
 * program : compound_statement
 * compound_statement : statement_list
 * statement_list : statement | statement (SEMI | NEW_LINE) statement_list
 * statement : compound_statement | assignment_statement | empty
 * assignment_statement : (LET | VAR) variable ASSIGN expr
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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import interpreter.ast.AbstractSyntaxTree;
import interpreter.ast.Assign;
import interpreter.ast.BinaryOperation;
import interpreter.ast.Compound;
import interpreter.ast.Number;
import interpreter.ast.UnaryOperation;
import interpreter.ast.Variable;

public class Interpreter {

    private Lexer lexer = new Lexer();
    private Token currentToken;

    public static HashMap<String, Integer> SYMBOL_TABLE = new HashMap<>();

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
        statements.add(statement());

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
        if (currentToken.type() == Token.Type.LET || currentToken.type() == Token.Type.VAR) {
            var statement = assignStatement();
            return statement;
        } else {
            throw new IllegalStateException("Выражение должно начинаться с var или let");
        }
    }

    private AbstractSyntaxTree assignStatement() {
        var type = currentToken;
        currentToken = lexer.readNextToken();

        if (currentToken.type() != Token.Type.ID) {
            throw new IllegalStateException(
                    "Некорректный токен: " + currentToken.type() + " Ожидалось имя переменной.");
        }

        var variableName = new Variable(type, currentToken);
        currentToken = lexer.readNextToken();

        if (currentToken.type() != Token.Type.ASSIGN) {
            throw new IllegalStateException(
                    "Некорректный токен: " + currentToken.type() + " Ожидался оператор присваивания.");
        }

        var assignToken = currentToken;
        currentToken = lexer.readNextToken();

        return new Assign(variableName, assignToken, expr());
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
            var node = new UnaryOperation(op, factor());
            return node;
        } else if (currentToken.type() == Token.Type.INTEGER) {
            var node = new Number(currentToken);
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.LPAREN) {
            currentToken = lexer.readNextToken();
            var node = expr();
            currentToken = lexer.readNextToken();
            return node;
        } else if (currentToken.type() == Token.Type.ID) {
            // небольшой хак, в rvalue все переменные пока что let, так как они там не
            // изменяются
            var node = new Variable(new Token(Token.Type.LET, "let"), currentToken);
            currentToken = lexer.readNextToken();
            return node;
        }

        throw new IllegalStateException(
                String.format("Не удалось получить значение оператора. Обрабатываемый токен: %s", currentToken.type()));
    }

    public static void main(String[] args) {
        try {
            Path contents = FileSystems
                    .getDefault()
                    .getPath("")
                    .resolve("interpreter")
                    .resolve("Contents_1000.swift");

            StringBuilder sb = new StringBuilder();
            List<String> lines = Files.readAllLines(contents);

            for (String line : lines) {
                sb.append(line);
                sb.append('\n');
            }

            var ast = new Interpreter().interpret(sb.toString().trim());
            System.out.println(ast.calculate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}