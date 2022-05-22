package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class Boolean implements Expression {
   private Token token;
   public boolean value;

   public Boolean(Token token, boolean value) {
      this.token = token;
      this.value = value;
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
