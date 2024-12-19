package interpreter.ast;


import java.util.HashMap;
import java.util.Map;

import interpreter.Token;

public record FunctionCall(String name, Map<Token, Token> args) implements AbstractSyntaxTree{
    @Override
    public AbstractSyntaxTree copy() {
        return new FunctionCall(name, new HashMap<>(args));
    }
}
