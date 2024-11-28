package interpreter.ast;

import java.util.List;

public record Compound(List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree {
}
