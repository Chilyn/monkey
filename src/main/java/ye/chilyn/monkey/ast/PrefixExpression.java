package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class PrefixExpression implements Expression {
   private Token token;
   public String operator;
   public Expression right;

   public PrefixExpression(Token token, String operator) {
      this.token = token;
      this.operator = operator;
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
      return "(" + operator + right.string() + ")";
   }
}
