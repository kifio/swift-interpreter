package interpreter.ast;


import java.util.List;

public record FunctionCall(List<AbstractSyntaxTree> statementList) implements AbstractSyntaxTree{
}
