package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class ReturnStatement implements Statement {
    private Token token;
    private Expression returnValue;

    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }
}
