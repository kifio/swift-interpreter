package interpreter.ast;

import java.util.List;

public record Compound(List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree {

    @Override
    public int calculate() {
        for (AbstractSyntaxTree statement : statementList) {
            statement.calculate();
        }

        return 0;
    }

}
