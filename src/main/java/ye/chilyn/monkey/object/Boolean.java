package ye.chilyn.monkey.object;

public class Boolean implements Object {
    public boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    @Override
    public String type() {
        return ObjectType.BOOLEAN_OBJ;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }
}
