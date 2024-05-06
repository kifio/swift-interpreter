package calc6;

/**
 * BinaryOperation
 */
public record BinaryOperation(
    AbstractSyntaxTree left,
    Token opToken,
    AbstractSyntaxTree right
) implements AbstractSyntaxTree {}
