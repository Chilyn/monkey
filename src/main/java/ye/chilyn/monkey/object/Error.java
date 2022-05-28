package ye.chilyn.monkey.object;

import java.lang.String;

public class Error implements Object {
    public String message;

    public Error(String message) {
        this.message = message;
    }

    @Override
    public String type() {
        return ObjectType.ERROR_OBJ;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }
}
