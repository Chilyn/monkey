package ye.chilyn.monkey.ast;

import java.util.List;

import ye.chilyn.monkey.Token;

public class BlockStatement implements Statement {
    private Token token;
    public List<Statement> statements;

    public BlockStatement(Token token) {
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
        StringBuilder sb  = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement.string());
        }
        return sb.toString();
    }
}
