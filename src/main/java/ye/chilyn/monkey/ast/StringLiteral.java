package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class StringLiteral implements Expression {
   private Token token;
   public String value;

   public StringLiteral(Token token, String value) {
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
