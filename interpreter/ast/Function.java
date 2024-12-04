package interpreter.ast;

import java.util.HashMap;

public record Function(HashMap<String, Constant> args) implements AbstractSyntaxTree {
}
