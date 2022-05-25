package ye.chilyn.monkey.object;

import java.util.HashMap;
import java.util.Map;

public class Environment {
   private Map<String, Object> store;

   public Environment() {
      this.store = new HashMap<>();
   }

   public Object get(String name) {
      return store.get(name);
   }

   public Object set(String name, Object val) {
      store.put(name, val);
      return val;
   }
}
