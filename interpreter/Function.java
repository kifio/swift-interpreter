package interpreter;

import interpreter.ast.AbstractSyntaxTree;
import interpreter.ast.Constant;

import java.util.HashMap;
import java.util.List;

public record Function(String name, HashMap<String, Constant> args, List<AbstractSyntaxTree> statementList) {
}
