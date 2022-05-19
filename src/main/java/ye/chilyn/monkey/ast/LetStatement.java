package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class LetStatement implements Statement {
    private Token token;
    private Identifier name;
    private Expression value;

    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }
}
