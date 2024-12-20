package interpreter;

import interpreter.ast.AbstractSyntaxTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Function {
    private final String name;
    private final Map<String, Identifier> args;
    private List<AbstractSyntaxTree> statementList;

    public Function(String name, Map<String, Identifier> args) {
        this.name = name;
        this.args = args;
    }

    public String name() {
        return name;
    }

    public Map<String, Identifier> args() {
        return args;
    }

    public List<AbstractSyntaxTree> getStatementList() {
        return statementList.stream().map(AbstractSyntaxTree::copy).toList();
    }

    public void setStatementList(List<AbstractSyntaxTree> statementList) {
        this.statementList = statementList;
    }
}
