package ye.chilyn.monkey.ast;

import ye.chilyn.monkey.Token;

public class LetStatement implements Statement {
    private Token token;
    public Identifier name;
    public Expression value;

    public LetStatement(Token token) {
        this.token = token;
    }

    public LetStatement(Token token, Identifier name, Expression value) {
        this.token = token;
        this.name = name;
        this.value = value;
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
        String result = tokenLiteral() + " " + name.string() + " = ";
        if (value != null) {
            result += value.string();
        }

        result += ";";
        return result;
    }
}
