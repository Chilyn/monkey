package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class LetStatement implements Statement {
    private Token token;
    public Identifier name;
    private Expression value;

    public LetStatement(Token token) {
        this.token = token;
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }
}
