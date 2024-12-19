package interpreter;

import interpreter.ast.AbstractSyntaxTree;

import java.util.HashMap;
import java.util.List;

public class Function {
    private String name;
    private HashMap<String, Identifier> args;
    private List<AbstractSyntaxTree> statementList;

    public Function(String name, HashMap<String, Identifier> args) {
        this.name = name;
        this.args = args;
    }

    public String name() {
        return name;
    }

    public HashMap<String, Identifier> args() {
        return args;
    }

    public List<AbstractSyntaxTree> getStatementList() {
        return statementList;
    }

    public void setStatementList(List<AbstractSyntaxTree> statementList) {
        this.statementList = statementList;
    }
}
