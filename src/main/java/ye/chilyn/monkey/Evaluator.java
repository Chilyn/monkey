package ye.chilyn.monkey;

import java.util.List;

import ye.chilyn.monkey.ast.BlockStatement;
import ye.chilyn.monkey.ast.ExpressionStatement;
import ye.chilyn.monkey.ast.IfExpression;
import ye.chilyn.monkey.ast.InfixExpression;
import ye.chilyn.monkey.ast.IntegerLiteral;
import ye.chilyn.monkey.ast.Node;
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.ReturnStatement;
import ye.chilyn.monkey.ast.Statement;
import ye.chilyn.monkey.object.Boolean;
import ye.chilyn.monkey.object.Integer;
import ye.chilyn.monkey.object.Null;
import ye.chilyn.monkey.object.Object;
import ye.chilyn.monkey.object.ObjectType;
import ye.chilyn.monkey.object.ReturnValue;

public class Evaluator {
    public static final Null NULL = new Null();
    private static final Boolean TRUE = new Boolean(true);
    private static final Boolean FALSE = new Boolean(false);

    public Object eval(Node node) {
        if (node instanceof Program) {
            return evalProgram((Program) node);
        } else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).expression);
        } else if (node instanceof IntegerLiteral) {
            return new Integer(((IntegerLiteral) node).value);
        } else if (node instanceof ye.chilyn.monkey.ast.Boolean) {
            return nativeBoolToBooleanObject(((ye.chilyn.monkey.ast.Boolean) node).value);
        } else if (node instanceof PrefixExpression) {
            Object right = eval(((PrefixExpression) node).right);
            return evalPrefixExpression(((PrefixExpression) node).operator, right);
        } else if (node instanceof InfixExpression) {
            Object left = eval(((InfixExpression) node).left);
            Object right = eval(((InfixExpression) node).right);
            return evalInfixExpression(((InfixExpression) node).operator, left, right);
        } else if (node instanceof BlockStatement) {
            return evalBlockStatement((BlockStatement) node);
        } else if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression) node);
        } else if (node instanceof ReturnStatement) {
            Object val = eval(((ReturnStatement) node).returnValue);
            return new ReturnValue(val);
        }

        return null;
    }

    private Object  evalProgram(Program program) {
        Object result = null;
        for (Statement statement : program.statements) {
            result = eval(statement);
            if (result instanceof ReturnValue) {
                return ((ReturnValue) result).value;
            }
        }

        return result;
    }

    private Object evalBlockStatement(BlockStatement block) {
        Object result = null;
        for (Statement statement : block.statements) {
            result = eval(statement);
            if (result != null && ObjectType.RETURN_VALUE_OBJ.equals(result.type())) {
                return result;
            }
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

    private Object evalInfixExpression(String operator, Object left, Object right) {
        if (ObjectType.INTEGER_OBJ.equals(left.type()) && ObjectType.INTEGER_OBJ.equals(right.type())) {
            return evalIntegerInfixExpression(operator, left, right);
        } else if ("==".equals(operator)) {
            return nativeBoolToBooleanObject(left == right);
        } else if ("!=".equals(operator)) {
            return nativeBoolToBooleanObject(left != right);
        } else {
            return NULL;
        }
    }

    private Object evalIntegerInfixExpression(String operator, Object left, Object right) {
        long leftVal = ((Integer) left).value;
        long rightVal = ((Integer) right).value;
        switch (operator) {
            case "+":
                return new Integer(leftVal + rightVal);
            case "-":
                return new Integer(leftVal - rightVal);
            case "*":
                return new Integer(leftVal * rightVal);
            case "/":
                return new Integer(leftVal / rightVal);
            case "<":
                return nativeBoolToBooleanObject(leftVal < rightVal);
            case ">":
                return nativeBoolToBooleanObject(leftVal > rightVal);
            case "==":
                return nativeBoolToBooleanObject(leftVal == rightVal);
            case "!=":
                return nativeBoolToBooleanObject(leftVal != rightVal);
            default:
                return NULL;
        }
    }

    private Object evalIfExpression(IfExpression ie) {
        Object condition = eval(ie.condition);
        if (isTruthy(condition)) {
            return eval(ie.consequence);
        } else if (ie.alternative != null) {
            return eval(ie.alternative);
        } else {
            return NULL;
        }
    }

    private boolean isTruthy(Object obj) {
        if (obj == NULL) {
            return false;
        } else if (obj == TRUE) {
            return true;
        } else if (obj == FALSE) {
            return false;
        } else {
            return true;
        }
    }
}
