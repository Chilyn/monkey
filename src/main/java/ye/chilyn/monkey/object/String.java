package ye.chilyn.monkey.object;

public class String implements Object {
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
}
