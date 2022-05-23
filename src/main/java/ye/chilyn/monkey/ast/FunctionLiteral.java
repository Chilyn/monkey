package ye.chilyn.monkey.ast;

import java.util.List;

import ye.chilyn.monkey.Token;

public class FunctionLiteral implements Expression {
    private Token token;
    public List<Identifier> parameters;
    public BlockStatement body;

    public FunctionLiteral(Token token) {
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
        sb.append(tokenLiteral());
        sb.append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i));
            if (i != parameters.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        sb.append(body.string());
        return sb.toString();
    }
}
