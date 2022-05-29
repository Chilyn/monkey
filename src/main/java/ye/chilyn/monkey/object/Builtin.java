package ye.chilyn.monkey.object;

public class Builtin implements Object {
   public BuiltinFunction fn;

   public Builtin(BuiltinFunction fn) {
      this.fn = fn;
   }

   @Override
   public java.lang.String type() {
      return ObjectType.BUILTIN_OBJ;
   }

   @Override
   public java.lang.String inspect() {
      return "builtin function";
   }
}
