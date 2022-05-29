package ye.chilyn.monkey.object;

import java.util.HashMap;
import java.util.Map;
import java.lang.String;

public class Environment {
    private Map<String, Object> store;
    private Environment outer;

    public Environment() {
        this.store = new HashMap<>();
    }

    public Environment(Environment outer) {
        this();
        this.outer = outer;
    }

    public Object get(String name) {
        Object obj = store.get(name);
        if (obj == null && outer != null) {
            obj = outer.get(name);
        }

        return obj;
    }

    public Object set(String name, Object val) {
        store.put(name, val);
        return val;
    }
}
