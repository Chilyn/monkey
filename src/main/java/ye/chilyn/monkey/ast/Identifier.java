package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class Identifier implements Expression {
    private Token token;
    public String value;

    public Identifier(Token token, String value) {
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
}
