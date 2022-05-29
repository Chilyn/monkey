package ye.chilyn.monkey.object;

import java.lang.String;
import java.util.List;

public class Array implements Object {
    public List<Object> elements;

    public Array(List<Object> elements) {
        this.elements = elements;
    }

    @Override
    public String type() {
        return ObjectType.ARRAY_OBJ;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).inspect());
            if (i != elements.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
