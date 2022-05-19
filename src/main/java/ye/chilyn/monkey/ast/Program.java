package ye.chilyn.monkey.ast;

public class Program implements Node {
    private Statement[] statements = {};

    @Override
    public String tokenLiteral() {
        if (statements.length > 0) {
           return statements[0].tokenLiteral();
        } else {
            return "";
        }
    }
}
