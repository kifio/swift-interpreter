package calc3;

import java.util.ArrayList;

// Простой калькулятор для вычисления арифметических выражений.
// Поддерживает сложение и вычитание с произвольным количеством аргументов,
// а также односложные умножение и деление. 
public class Interpreter {

    private int pos;
    private Token currentToken;
    private char[] text;

    public static void main(String[] args) {
        // String expression = args[1];
        Interpreter interpreter = new Interpreter();
        System.out.println(interpreter.expr("13*4"));
    }

    private void readNextToken() {
        skipWhiteSpace();

        if (pos > text.length - 1) {
            currentToken = new Token(Token.Type.EOF, null);
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
                Token.Type.INTEGER,
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
                new char[]{currentChar}
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

    // Читаем операнд
    private int term() {
        var value = currentToken.value();
        eat(Token.Type.INTEGER);
        return Integer.parseInt(new String(value));
    }

    public int expr(String expression) {

        text = expression.toCharArray();
        pos = 0;
        currentToken = null;

        readNextToken();

        var result = term();

        while (currentToken.isOperation()) {
            if (currentToken.type() == Token.Type.MUL) {
                readNextToken();
                result *= term();
            } else if (currentToken.type() == Token.Type.DIV) {
                readNextToken();
                result /= term();
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return result;
    }
}
