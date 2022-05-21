package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class IntegerLiteral implements Expression {
   private Token token;
   public long value;

   public IntegerLiteral(Token token) {
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
      return token.getLiteral();
   }
}
