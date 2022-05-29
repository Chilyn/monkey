package ye.chilyn.monkey.ast;

import java.util.List;

import ye.chilyn.monkey.Token;

public class ArrayLiteral implements Expression {
   private Token token;
   public List<Expression> elements;

   public ArrayLiteral(Token token) {
      this.token = token;
   }

   @Override
   public void expressionNode() {

   }

   @Override
   public String tokenLiteral() {
      return token.getLiteral();
   }

   @Override
   public String string() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      for (int i = 0; i < elements.size(); i++) {
         sb.append(elements.get(i).string());
         if (i != elements.size() - 1) {
            sb.append(", ");
         }
      }
      sb.append("]");
      return sb.toString();
   }
}
