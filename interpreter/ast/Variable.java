package interpreter.ast;

import interpreter.Token;

public record Variable(Token type, Token value) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculate'");
    }

}
