package interpreter;

import interpreter.ast.AbstractSyntaxTree;

import java.util.HashMap;
import java.util.List;

public record Function(String name, HashMap<String, Identifier> args, List<AbstractSyntaxTree> statementList) {}
