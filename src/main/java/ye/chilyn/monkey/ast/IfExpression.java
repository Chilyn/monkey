package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class IfExpression implements Expression {
    private Token token;
    public Expression condition;
    public BlockStatement consequence;
    public BlockStatement alternative;

    public IfExpression(Token token) {
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
        sb.append("if");
        sb.append(condition.string());
        sb.append(" ");
        sb.append(consequence.string());
        if (alternative != null) {
            sb.append("else ");
            sb.append(alternative.string());
        }
        return sb.toString();
    }
}
