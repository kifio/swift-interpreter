package interpreter;

import java.util.ArrayList;
import java.util.HashMap;

class Lexer {

    private int pos;
    private char[] text;

    private final HashMap<String, Token> variables = new HashMap<>();
    private final HashMap<String, Token> keyWords = new HashMap<>();

    {
        keyWords.put(KeyWord.LET, new Token(Token.Type.LET, KeyWord.LET));
        keyWords.put(KeyWord.VAR, new Token(Token.Type.LET, KeyWord.VAR));
    }

    void initialize(String expression) {
        variables.clear();
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
        } else if (((text[pos] >= 'a' && text[pos] <= 'z') || (text[pos] >= 'A' && text[pos] <= 'Z'))) {
            return readWord(text[pos]);
        } else {
            return readSign(text[pos]);
        }
    }

    // Возвращает следующий символ не увеличивая позицию обрабатываемого символа
    private char peek() {
        if (pos + 1 > text.length - 1) {
            return ' ';
        } else {
            return text[pos + 1];
        }
    }

    private void skipWhiteSpace() {
        while (pos <= text.length - 1 && text[pos] == ' ') {
            pos += 1;
        }
    }

    private Token readWord(char currentChar) {
        var letterCharacters = new ArrayList<Character>();
        letterCharacters.add(currentChar);
        pos += 1;

        while (pos <= text.length - 1 && ((text[pos] >= 'a' && text[pos] <= 'z') || (text[pos] >= 'A' && text[pos] <= 'Z'))) {
            letterCharacters.add(text[pos]);
            pos += 1;
        }

        char[] arr = new char[letterCharacters.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = letterCharacters.get(i);
        }

        String word = new String(arr);

        if (keyWords.containsKey(word)) {
            return keyWords.get(word);
        } else if (variables.containsKey(word)) {
            return variables.get(word);
        } else {
            var token = new Token(Token.Type.ID, word);
            variables.put(word, token);
            return token;
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
            new String(arr)
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
            case '(':
                type = Token.Type.LPAREN;
                break;
            case ')':
                type = Token.Type.RPAREN;
                break;
            case '=':
                type = Token.Type.ASSIGN;
                break;
            case '\n':  // Есть опасения, что в Windows не заработает тк там \t\n.
                type = Token.Type.NEW_LINE;
                break;
            case ';':
                type = Token.Type.SEMI;
                break;
            default:
                throw new IllegalStateException(String.format("Неподдерживаемый символ %s позиция %d", currentChar, pos));
        }

        pos += 1;

        return new Token(
            type, 
            new String(new char[]{ currentChar })
        );
    }
}
