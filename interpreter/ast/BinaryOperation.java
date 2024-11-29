package interpreter.ast;

import interpreter.Token;

public record BinaryOperation(AbstractSyntaxTree left, Token opToken,
                              AbstractSyntaxTree right) implements AbstractSyntaxTree {
}
