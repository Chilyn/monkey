package ye.chilyn.monkey.object;

import java.util.Objects;

public class HashKey {
   public java.lang.String type;
   public int value;

   public HashKey(java.lang.String type, int value) {
      this.type = type;
      this.value = value;
   }

   @Override
   public boolean equals(java.lang.Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      HashKey hashKey = (HashKey) o;
      return value == hashKey.value && Objects.equals(type, hashKey.type);
   }

   @Override
   public int hashCode() {
      return value;
   }
}
