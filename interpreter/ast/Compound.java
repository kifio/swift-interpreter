package interpreter.ast;

import java.util.List;

public record Compound(List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree {
    @Override
    public AbstractSyntaxTree copy() {
        return new Compound(statementList.stream().map(AbstractSyntaxTree::copy).toList());
    }
}
