package ye.chilyn.monkey.object;

import java.lang.String;

public class Integer implements Object, HashTable {
    public long value;

    public Integer(long value) {
        this.value = value;
    }

    @Override
    public String type() {
        return ObjectType.INTEGER_OBJ;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }

    @Override
    public HashKey hashKey() {
        return new HashKey(type(), (int) value);
    }
}
