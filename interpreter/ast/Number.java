package interpreter.ast;

import interpreter.Token;

public record Number(Token token) implements AbstractSyntaxTree {
}
