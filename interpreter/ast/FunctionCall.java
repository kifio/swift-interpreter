package interpreter.ast;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.Token;

public record FunctionCall(String name, Map<Token, Token> args, List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree{
    @Override
    public AbstractSyntaxTree copy() {
        return new FunctionCall(name, new HashMap<>(args), statementList.stream().map(AbstractSyntaxTree::copy).toList());
    }
}
