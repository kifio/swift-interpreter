package interpreter.ast;

import interpreter.Token;

public record UnaryOperation(
        Token opToken,
        AbstractSyntaxTree right
) implements AbstractSyntaxTree {
}
