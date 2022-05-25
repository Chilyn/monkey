package ye.chilyn.monkey.object;

public class ReturnValue implements Object {
    public Object value;

    public ReturnValue(Object value) {
        this.value = value;
    }

    @Override
    public String type() {
        return ObjectType.RETURN_VALUE_OBJ;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }
}
