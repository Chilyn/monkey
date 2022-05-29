package ye.chilyn.monkey.test;

import static ye.chilyn.monkey.Printer.println;

import ye.chilyn.monkey.object.HashKey;
import ye.chilyn.monkey.object.String;

public class ObjectTest {
   public void testStringHashKey() {
      HashKey hello1 = new String("Hello World").hashKey();
      HashKey hello2 = new String("Hello World").hashKey();
      HashKey diff1 = new String("My name is johnny").hashKey();
      HashKey diff2 = new String("My name is johnny").hashKey();
      if (hello1.value != hello2.value) {
         println("strings with same content have different hash keys");
      }

      if (diff1.value != diff2.value) {
         println("strings with same content have different hash keys");
      }

      if (hello1.value == diff1.value) {
         println("strings with different content have same hash keys");
      }
      println("success");
   }
}
