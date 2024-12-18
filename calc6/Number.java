package calc6;

record Number(Token token) implements AbstractSyntaxTree {
    @Override
    public int calculate() {
        return Integer.parseInt(new String(token.value()));
    }
}
