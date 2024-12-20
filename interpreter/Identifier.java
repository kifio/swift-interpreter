package interpreter;

public class Identifier {

    public enum DataType {
        INTEGER, DOUBLE
    }

    private static DataType dataType(Token type) {
        if (type == Token.INTEGER) {
            return DataType.INTEGER;
        } else if (type == Token.DOUBLE) {
            return DataType.DOUBLE;
        } else {
            return null;
        }
    }

    private final String name;
    private Double value;
    private DataType dataType;
    private final boolean isConstant;
    private boolean isInitialized;

    public Identifier(Token name, Token dataType, boolean isConstant) {
        this.name = name.value();
        this.dataType = dataType(dataType);
        this.isConstant = isConstant;
    }
    public Identifier(Identifier identifier) {
        this.name = identifier.name;
        this.value = identifier.value;
        this.dataType = identifier.dataType;
        this.isConstant = identifier.isConstant;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.isInitialized = true;
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean canBeAssigned() {
        return !isConstant || !isInitialized;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", dataType=" + dataType +
                ", isConstant=" + isConstant +
                '}';
    }
}
