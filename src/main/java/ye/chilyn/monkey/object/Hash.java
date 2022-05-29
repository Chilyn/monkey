package ye.chilyn.monkey.object;

import java.util.Map;

public class Hash implements Object {
   public Map<HashKey, HashPair> pairs;

   public Hash(Map<HashKey, HashPair> pairs) {
      this.pairs = pairs;
   }

   @Override
   public java.lang.String type() {
      return ObjectType.HASH_OBJ;
   }

   @Override
   public java.lang.String inspect() {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      int index = 0;
      for (Map.Entry<HashKey, HashPair> entry : pairs.entrySet()) {
         sb.append(entry.getValue().key.inspect());
         sb.append(": ");
         sb.append(entry.getValue().value.inspect());
         if (index != pairs.size() - 1) {
            sb.append(", ");
         }
         index++;
      }
      sb.append("}");
      return sb.toString();
   }
}
