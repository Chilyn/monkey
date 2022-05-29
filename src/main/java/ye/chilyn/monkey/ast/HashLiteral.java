package ye.chilyn.monkey.ast;

import java.util.List;
import java.util.Map;

import ye.chilyn.monkey.Token;

public class HashLiteral implements Expression {
   private Token token;
   public Map<Expression, Expression> pairs;

   public HashLiteral(Token token) {
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
      sb.append("{");
      for (Expression key : pairs.keySet()) {
         sb.append(key.string());
         sb.append(":");
         sb.append(pairs.get(key).string());
      }
      sb.append("}");
      return sb.toString();
   }
}
