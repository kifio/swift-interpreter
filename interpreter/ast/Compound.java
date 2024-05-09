package interpreter.ast;

import java.util.List;

public record Compound(List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        throw new UnsupportedOperationException("Unimplemented method 'calculate'");
    }

}
