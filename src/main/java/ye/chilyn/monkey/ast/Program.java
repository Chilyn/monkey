package ye.chilyn.monkey.ast;

import java.util.ArrayList;
import java.util.List;

public class Program implements Node {
    public List<Statement> statements = new ArrayList<>();

    @Override
    public String tokenLiteral() {
        if (statements.size() > 0) {
           return statements.get(0).tokenLiteral();
        } else {
            return "";
        }
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        for (Statement s : statements) {
            sb.append(s.string());
        }
        return sb.toString();
    }
}
