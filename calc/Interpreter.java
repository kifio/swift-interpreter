package calc;

import java.util.ArrayList;

// Очень простой калькулятор для односложного сложения и вычитания
// x+x, x-x
public class Interpreter {

    private int pos;
    private Token currentToken;
    private char[] text;

    private void readNextToken() {
        while (pos <= text.length - 1 && text[pos] == ' ') {
            pos += 1;
        }

        if (pos > text.length - 1) {
            currentToken = new Token(calc.Token.Type.EOF, null);
            return;
        }

        var currentChar = text[pos];

        if (Character.isDigit(currentChar)) {
            readInteger(currentChar);
        } else {
            readSign(currentChar);
        }
    }

    private void readInteger(char currentChar) {
        var digitCharacters = new ArrayList<Character>();
        digitCharacters.add(currentChar);
        pos += 1;

        while (pos <= text.length - 1 && Character.isDigit(text[pos])) {
            digitCharacters.add(text[pos]);
            pos += 1;
        }

        char[] arr = new char[digitCharacters.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = digitCharacters.get(i);
        }

        currentToken = new Token(
            calc.Token.Type.INTEGER, 
            arr
        );
    }

    private void readSign(char currentChar) {
        Token.Type type = null;

        switch (currentChar) {
            case '+':
                type = Token.Type.PLUS;
                break;
            case '-':
                type = Token.Type.MINUS;
                break;
            default:
                throw new IllegalStateException(String.format("Неподдерживаемый символ %s позиция %d", currentChar, pos));
        }

        currentToken = new Token(
            type, 
            new char[]{ currentChar }
        );

        pos += 1;
    }

    private void eat(Token.Type type) {
        if (currentToken.type() == type) {
            readNextToken(); 
        } else {
            throw new IllegalStateException(
                String.format("Ожидаемый токен %s, но на самом деле %s", type.name(), currentToken.type())
            );
        }
    }
    
    public int expr(String expression) {

        text = expression.toCharArray();
        pos = 0;
        currentToken = null;

        readNextToken();

        var left = currentToken;
        eat(Token.Type.INTEGER);

        var leftValue = Integer.parseInt(
            new String(left.value())
        );

        var op = currentToken;
        
        if (op.type() == Token.Type.PLUS || op.type() == Token.Type.MINUS) {
            eat(op.type());
        }
        
        var right = currentToken;
        eat(Token.Type.INTEGER);

        var rightValue = Integer.parseInt(
            new String(right.value())
        );

        if (op.type() == Token.Type.PLUS) {
            return leftValue + rightValue; 
        } else if (op.type() == Token.Type.MINUS) {
            return leftValue - rightValue; 
        } else {
            throw new IllegalStateException("Неподдерживаемый оператор" + op.type());
        }
    }
    
    public static void main(String[] args) {
        // String expression = args[1];
        Interpreter interpreter = new Interpreter();
        System.out.println(interpreter.expr(" 1  + 1 "));
    }
}
