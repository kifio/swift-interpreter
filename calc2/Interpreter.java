package calc2;

import java.util.ArrayList;

// Простой калькулятор для вычисления арифметических выражений.
// Поддерживает сложение и вычитание с произвольным количеством аргументов,
// а также односложные умножение и деление. 
public class Interpreter {

    private int pos;
    private Token currentToken;
    private char[] text;

    private void readNextToken() {
        skipWhiteSpace();

        if (pos > text.length - 1) {
            currentToken = new Token(calc2.Token.Type.EOF, null);
            return;
        }
        
        if (Character.isDigit(text[pos])) {
            readInteger(text[pos]);
        } else {
            readSign(text[pos]);
        }
    }

    private void skipWhiteSpace() {
        while (pos <= text.length - 1 && text[pos] == ' ') {
            pos += 1;
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
            calc2.Token.Type.INTEGER, 
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
            case '*':
                type = Token.Type.MUL;
                break;
            case '/':
                type = Token.Type.DIV;
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

        do {
            var op = currentToken;
        
            if (validOperation(op.type())) {
                eat(op.type());
            }
            
            var right = currentToken;
            eat(Token.Type.INTEGER);
    
            var rightValue = Integer.parseInt(
                new String(right.value())
            );
    
            if (op.type() == Token.Type.PLUS) {
                leftValue += rightValue; 
            } else if (op.type() == Token.Type.MINUS) {
                leftValue -= rightValue; 
            } else if (op.type() == Token.Type.MUL) {
                leftValue *= rightValue; 
            } else if (op.type() == Token.Type.DIV) {
                leftValue /= rightValue; 
            } else {
                throw new IllegalStateException("Неподдерживаемый оператор" + op.type());
            }
        } while (currentToken.type() !=  Token.Type.EOF);

        return leftValue;
    }

    private boolean validOperation(Token.Type type) {
        return type == Token.Type.PLUS 
        || type == Token.Type.MINUS
        || type == Token.Type.MUL
        || type == Token.Type.DIV;
    }
    
    public static void main(String[] args) {
        // String expression = args[1];
        Interpreter interpreter = new Interpreter();
        System.out.println(interpreter.expr("13*4"));
    }
}
