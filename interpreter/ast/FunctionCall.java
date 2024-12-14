package interpreter.ast;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record FunctionCall(String name, Map<String, Constant> args, List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree{
    @Override
    public AbstractSyntaxTree copy() {
        return new FunctionCall(name, new HashMap<>(args), statementList.stream().map(AbstractSyntaxTree::copy).toList());
    }
}
