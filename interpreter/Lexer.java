package interpreter;

import java.util.ArrayList;
import java.util.HashMap;

class Lexer {

    private final HashMap<String, Token> keyWords = new HashMap<>();
    private int pos;
    private char[] text;

    {
        keyWords.put(Token.LET.value(), Token.LET);
        keyWords.put(Token.VAR.value(), Token.VAR);
        keyWords.put(Token.INTEGER.value(), Token.INTEGER);
        keyWords.put(Token.DOUBLE.value(), Token.DOUBLE);
        keyWords.put(Token.FUNC.value(), Token.FUNC);
    }

    void initialize(String expression) {
        text = expression.toCharArray();
        pos = 0;
    }

    Token readNextToken() {
        skipWhiteSpace();
        skipComment();

        if (pos > text.length - 1) {
            return Token.EOF;
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
            } while (pos <= text.length - 1 && text[pos] != '\n');

        } else if (text[pos] == '*') {
            do {
                pos++;
            } while (pos <= text.length - 1 && !(text[pos] == '*' && text[pos + 1] == '/'));
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
        } else {
            return new Token(Token.Type.ID, word);
        }
    }

    private Token readNumber(char currentChar) {
        var digitCharacters = new ArrayList<Character>();
        digitCharacters.add(currentChar);
        pos += 1;

        while (pos <= text.length - 1 && (Character.isDigit(text[pos]) || text[pos] == '.')) {
            digitCharacters.add(text[pos]);
            if (text[pos] == '.') {
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

        return new Token(Token.Type.NUMBER, new String(arr));
    }

    private Token readSign(char currentChar) {
        return switch (currentChar) {
            case '+' -> {
                pos += 1;
                yield Token.PLUS;
            }
            case '-' -> {
                pos += 1;
                yield Token.MINUS;
            }
            case '*' -> {
                pos += 1;
                yield Token.MUL;
            }
            case '/' -> {
                pos += 1;
                yield Token.DIV;
            }
            case '(' -> {
                pos += 1;
                yield Token.LPAREN;
            }
            case ')' -> {
                pos += 1;
                yield Token.RPAREN;
            }
            case '=' -> {
                pos += 1;
                yield Token.ASSIGN;
            }
            case '\n' -> {
                pos += 1;
                yield Token.NEW_LINE;
            }
            case ';' -> {
                pos += 1;
                yield Token.SEMI;
            }
            case ':' -> {
                pos += 1;
                yield Token.COLON;
            }
            case '{' -> {
                pos += 1;
                yield Token.OPENING_CURLY_BRACE;
            }
            case '}' -> {
                pos += 1;
                yield Token.CLOSING_CURLY_BRACE;
            }
            case ',' -> {
                pos += 1;
                yield Token.COMMA;
            }
            default ->
                    throw new IllegalStateException(String.format("Неподдерживаемый символ %s позиция %d", currentChar, pos));
        };
    }

    private boolean isPartOfWord(char c) {
        return c == '_' || ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }
}
