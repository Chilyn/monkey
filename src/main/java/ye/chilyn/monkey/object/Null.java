package ye.chilyn.monkey.object;

public class Null implements Object {
    @Override
    public String type() {
        return ObjectType.NULL_OBJ;
    }

    @Override
    public String inspect() {
        return "null";
    }
}
