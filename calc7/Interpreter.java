package calc7;

// Простой калькулятор для вычисления арифметических выражений.
// Поддерживает арифметические выражения без скобок.
// Грамматика:
// 
// expr: term((SUM|SUB)mod)*
// term: factor((MUL|DIV)factor)*
// factor: integer | (LPAREN expr RPAREN)

public class Interpreter {

    private Lexer lexer = new Lexer();
    private Token currentToken;

    public AbstractSyntaxTree interpret(String expression) {
        lexer.initialize(expression);
        currentToken = lexer.readNextToken();
        return expr();
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
        }

        throw new IllegalStateException(
            String.format("Не удалось получить значение оператора. Обрабатываемый токен: %s", currentToken.type())
        );
    }
    
    public static void main(String[] args) {
    }
}
