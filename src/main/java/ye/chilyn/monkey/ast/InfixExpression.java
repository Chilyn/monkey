package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class InfixExpression implements Expression {
   private Token token;
   public Expression left;
   public String operator;
   public Expression right;

   public InfixExpression(Token token, Expression left, String operator) {
      this.token = token;
      this.left = left;
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
      return "(" + left.string() + " " + operator + " " + right.string() + ")";
   }
}
