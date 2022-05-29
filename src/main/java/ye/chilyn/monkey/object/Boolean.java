package ye.chilyn.monkey.object;

import java.lang.String;

public class Boolean implements Object, HashTable {
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

    @Override
    public HashKey hashKey() {
        return new HashKey(type(), value ? 1 : 0);
    }
}
