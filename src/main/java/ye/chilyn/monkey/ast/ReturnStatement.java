package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class ReturnStatement implements Statement {
    private Token token;
    private Expression returnValue;

    public ReturnStatement(Token token) {
        this.token = token;
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        String result = tokenLiteral() + " ";
        if (returnValue != null) {
            result += returnValue.string();
        }

        result += ";";
        return result;
    }
}
