package ye.chilyn.monkey.object;

import java.util.List;
import java.lang.String;

import ye.chilyn.monkey.ast.BlockStatement;
import ye.chilyn.monkey.ast.Identifier;

public class Function implements Object {
    public List<Identifier> parameters;
    public BlockStatement body;
    public Environment env;

    public Function(List<Identifier> parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    @Override
    public String type() {
        return ObjectType.FUNCTION_OBJ;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("fn(");
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
