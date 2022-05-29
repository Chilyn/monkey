package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class IndexExpression implements Expression {
   private Token token;
   public Expression left;
   public Expression index;

   public IndexExpression(Token token, Expression left) {
      this.token = token;
      this.left = left;
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
      return "(" + left.string() + "[" + index.string() + "])";
   }
}
