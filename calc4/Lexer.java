package calc4;

import java.util.ArrayList;

class Lexer {

    private int pos;
    private char[] text;

    void initialize(String expression) {
        text = expression.toCharArray();
        pos = 0;
    }

    Token readNextToken() {
        skipWhiteSpace();

        if (pos > text.length - 1) {
            return new Token(Token.Type.EOF, null);
        }
        
        if (Character.isDigit(text[pos])) {
            return readInteger(text[pos]);
        } else {
            return readSign(text[pos]);
        }
    }

    private void skipWhiteSpace() {
        while (pos <= text.length - 1 && text[pos] == ' ') {
            pos += 1;
        }
    }

    private Token readInteger(char currentChar) {
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

        return new Token(
            Token.Type.INTEGER, 
            arr
        );
    }

    private Token readSign(char currentChar) {
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

        pos += 1;

        return new Token(
            type, 
            new char[]{ currentChar }
        );
    }
}
