package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class ExpressionStatement implements Statement {
    private Token token;
    public Expression expression;

    public ExpressionStatement(Token token) {
        this.token = token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String string() {
        String result = "";
        if (expression != null) {
            result += expression.string();
        }

        return result;
    }
}
