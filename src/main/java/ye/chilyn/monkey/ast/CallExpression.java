package ye.chilyn.monkey.ast;

import java.util.List;

import ye.chilyn.monkey.Token;

public class CallExpression implements Expression {
    private Token token;
    public Expression function;
    public List<Expression> arguments;

    public CallExpression(Token token, Expression function) {
        this.token = token;
        this.function = function;
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
        sb.append(function.string());
        sb.append("(");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i).string());
            if (i != arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
