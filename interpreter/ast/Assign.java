package interpreter.ast;

import interpreter.Token;

public record Assign(AbstractSyntaxTree left, Token opToken, AbstractSyntaxTree right) implements AbstractSyntaxTree {
}
