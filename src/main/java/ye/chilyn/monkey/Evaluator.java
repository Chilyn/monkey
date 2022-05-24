package ye.chilyn.monkey;

import java.util.List;

import ye.chilyn.monkey.ast.ExpressionStatement;
import ye.chilyn.monkey.ast.IntegerLiteral;
import ye.chilyn.monkey.ast.Node;
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.Statement;
import ye.chilyn.monkey.object.Boolean;
import ye.chilyn.monkey.object.Integer;
import ye.chilyn.monkey.object.Null;
import ye.chilyn.monkey.object.Object;
import ye.chilyn.monkey.object.ObjectType;

public class Evaluator {
    private static final Null NULL = new Null();
    private static final Boolean TRUE = new Boolean(true);
    private static final Boolean FALSE = new Boolean(false);

    public Object eval(Node node) {
        if (node instanceof Program) {
            return evalStatements(((Program) node).statements);
        } else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).expression);
        } else if (node instanceof IntegerLiteral) {
            return new Integer(((IntegerLiteral) node).value);
        } else if (node instanceof ye.chilyn.monkey.ast.Boolean) {
            return nativeBoolToBooleanObject(((ye.chilyn.monkey.ast.Boolean) node).value);
        } else if (node instanceof PrefixExpression) {
            Object right = eval(((PrefixExpression) node).right);
            return evalPrefixExpression(((PrefixExpression) node).operator, right);
        }

        return null;
    }

    private Object evalStatements(List<Statement> stmts) {
        Object result = null;
        for (Statement statement : stmts) {
            result = eval(statement);
        }

        return result;
    }

    private Boolean nativeBoolToBooleanObject(boolean input) {
        if (input) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private Object evalPrefixExpression(String operator, Object right) {
        switch (operator) {
            case "!":
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusPrefixOperatorExpression(right);
            default:
                return NULL;
        }
    }

    private Object evalBangOperatorExpression(Object right) {
        if (right == TRUE) {
            return FALSE;
        } else if (right == FALSE) {
            return TRUE;
        } else if (right == NULL) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private Object evalMinusPrefixOperatorExpression(Object right) {
        if (!ObjectType.INTEGER_OBJ.equals(right.type())) {
            return NULL;
        }

        long value = ((Integer) right).value;
        return new Integer(-value);
    }
}
