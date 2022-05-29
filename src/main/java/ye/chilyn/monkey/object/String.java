package ye.chilyn.monkey.object;

public class String implements Object, HashTable {
   public java.lang.String value;

   public String(java.lang.String value) {
      this.value = value;
   }

   @Override
   public java.lang.String type() {
      return ObjectType.STRING_OBJ;
   }

   @Override
   public java.lang.String inspect() {
      return value;
   }

   @Override
   public HashKey hashKey() {
      return new HashKey(type(), value.hashCode());
   }
}
