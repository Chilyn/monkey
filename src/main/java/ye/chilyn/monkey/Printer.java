package ye.chilyn.monkey;

public class Printer {
   public static void print(String msg) {
      System.out.print(msg);
   }

   public static void print(Object obj) {
      System.out.print(obj.toString());
   }

   public static void println(String msg) {
      System.out.println(msg);
   }

   public static void println(Object obj) {
      System.out.println(obj.toString());
   }
}
