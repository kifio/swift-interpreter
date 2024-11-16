package interpreter;

import java.util.ArrayList;
import java.util.HashMap;

class Lexer {

    private final HashMap<String, Token> variables = new HashMap<>();
    private final HashMap<String, Token> keyWords = new HashMap<>();
    private int pos;
    private char[] text;

    {
        keyWords.put(KeyWord.LET, new Token(Token.Type.LET, KeyWord.LET));
        keyWords.put(KeyWord.VAR, new Token(Token.Type.VAR, KeyWord.VAR));
    }

    void initialize(String expression) {
        variables.clear();
        text = expression.toCharArray();
        pos = 0;
    }

    Token readNextToken() {
        skipWhiteSpace();
        skipComment();

        if (pos > text.length - 1) {
            return new Token(Token.Type.EOF, null);
        }

        if (Character.isDigit(text[pos])) {
            return readNumber(text[pos]);
        } else if (isPartOfWord(text[pos])) {
            return readWord(text[pos]);
        } else {
            return readSign(text[pos]);
        }
    }

    private void skipComment() {
        // Если прочитал // или /*, то обрабатывать комментарий соотв. образом
        if (pos > text.length - 1 || text[pos] != '/') {
            return;
        }

        pos++;

        if (text[pos] == '/') {
            do {
                pos++;
            } while (text[pos] != '\n' && pos <= text.length - 1);

        } else if (text[pos] == '*') {
            do {
                pos++;
            } while (!(text[pos] == '*' && text[pos + 1] == '/') && pos <= text.length - 1);
            pos += 2;
        } else {
            pos--;
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

        while (pos <= text.length - 1 && (Character.isDigit(text[pos]) || isPartOfWord(text[pos]))) {
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

    private Token readNumber(char currentChar) {
        var digitCharacters = new ArrayList<Character>();
        digitCharacters.add(currentChar);
        pos += 1;

        Token.Type type = Token.Type.INTEGER;

        while (pos <= text.length - 1 && (Character.isDigit(text[pos]) || text[pos] == '.')) {
            digitCharacters.add(text[pos]);
            if (text[pos] == '.') {
                type = Token.Type.DOUBLE;
                pos += 1;
                while (pos <= text.length - 1 && Character.isDigit(text[pos])) {
                    digitCharacters.add(text[pos]);
                    pos += 1;
                }
            }
            pos += 1;
        }

        char[] arr = new char[digitCharacters.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = digitCharacters.get(i);
        }

        return new Token(
                type,
                new String(arr)
        );
    }

    private Token readSign(char currentChar) {
        Token.Type type;

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
                pos += 1;
                return Token.NEW_LINE;
            case ';':
                pos += 1;
                return Token.SEMI;
            case ':':
                pos += 1;
                return Token.COLON;
            default:
                throw new IllegalStateException(String.format("Неподдерживаемый символ %s позиция %d", currentChar, pos));
        }

        pos += 1;

        return new Token(
                type,
                String.valueOf(currentChar)
        );
    }

    private boolean isPartOfWord(char c) {
        return c == '_' || ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }
}
