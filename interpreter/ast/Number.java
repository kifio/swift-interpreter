package interpreter.ast;

import interpreter.Token;

public record Number(Token token) implements AbstractSyntaxTree {
    @Override
    public int calculate() {
        return Integer.parseInt(new String(token.value()));
    }
}
