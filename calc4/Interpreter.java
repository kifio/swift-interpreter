package calc4;

// Простой калькулятор для вычисления арифметических выражений.
// Поддерживает арифметические выражения без скобок.
// Грамматика:
// expr: mod((SUM|SUB)mod)*
// mod: factor((MUL|DIV)factor)*
// factor: integer

public class Interpreter {

    private Lexer lexer = new Lexer();
    private Token currentToken;

    public int interpret(String expression) {
        lexer.initialize(expression);
        currentToken = lexer.readNextToken();
        return expr();
    }

    private int expr() {
        var result = mulOrDiv();

        while (currentToken.isSumOrSub()) {
            if (currentToken.type() == Token.Type.PLUS) {
                currentToken = lexer.readNextToken(); 
                result += mulOrDiv();
            } else if (currentToken.type() == Token.Type.MINUS) {
                currentToken = lexer.readNextToken(); 
                result -= mulOrDiv();
            } else {
                throw new UnsupportedOperationException(
                    String.format("Ожидаемый токен + или -, но на самом деле %s", currentToken.type())
                );
            }
        }

        return result;
    }

    private int factor() {
        var value = currentToken.value();
        if (currentToken.type() == Token.Type.INTEGER) {
            currentToken = lexer.readNextToken(); 
        } else {
            throw new IllegalStateException(
                String.format("Ожидаемый токен %s, но на самом деле %s", Token.Type.INTEGER, currentToken.type())
            );
        }
        return Integer.parseInt(new String(value));
    }
    
    private int mulOrDiv() {
        var result = factor();

        while (currentToken.isMulOrDiv()) {
            if (currentToken.type() == Token.Type.MUL) {
                currentToken = lexer.readNextToken(); 
                result *= factor();
            } else if (currentToken.type() == Token.Type.DIV) {
                currentToken = lexer.readNextToken(); 
                result /= factor();
            } else {
                throw new UnsupportedOperationException(
                    String.format("Ожидаемый токен * или /, но на самом деле %s", currentToken.type())
                );
            }
        }

        return result;
    }
    
    public static void main(String[] args) {
   
    }
}
