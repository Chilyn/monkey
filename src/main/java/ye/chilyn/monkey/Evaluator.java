package ye.chilyn.monkey;

import java.util.ArrayList;
import java.util.List;

import ye.chilyn.monkey.ast.BlockStatement;
import ye.chilyn.monkey.ast.CallExpression;
import ye.chilyn.monkey.ast.Expression;
import ye.chilyn.monkey.ast.ExpressionStatement;
import ye.chilyn.monkey.ast.FunctionLiteral;
import ye.chilyn.monkey.ast.Identifier;
import ye.chilyn.monkey.ast.IfExpression;
import ye.chilyn.monkey.ast.InfixExpression;
import ye.chilyn.monkey.ast.IntegerLiteral;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.Node;
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.ReturnStatement;
import ye.chilyn.monkey.ast.Statement;
import ye.chilyn.monkey.ast.StringLiteral;
import ye.chilyn.monkey.object.Boolean;
import ye.chilyn.monkey.object.Environment;
import ye.chilyn.monkey.object.Error;
import ye.chilyn.monkey.object.Function;
import ye.chilyn.monkey.object.Integer;
import ye.chilyn.monkey.object.Null;
import ye.chilyn.monkey.object.Object;
import ye.chilyn.monkey.object.ObjectType;
import ye.chilyn.monkey.object.ReturnValue;
import ye.chilyn.monkey.object.String;

public class Evaluator {
    public static final Null NULL = new Null();
    private static final Boolean TRUE = new Boolean(true);
    private static final Boolean FALSE = new Boolean(false);

    public Object eval(Node node, Environment env) {
        if (node instanceof Program) {
            return evalProgram((Program) node, env);
        } else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).expression, env);
        } else if (node instanceof IntegerLiteral) {
            return new Integer(((IntegerLiteral) node).value);
        } else if (node instanceof ye.chilyn.monkey.ast.Boolean) {
            return nativeBoolToBooleanObject(((ye.chilyn.monkey.ast.Boolean) node).value);
        } else if (node instanceof PrefixExpression) {
            Object right = eval(((PrefixExpression) node).right, env);
            if (isError(right)) {
                return right;
            }
            return evalPrefixExpression(((PrefixExpression) node).operator, right);
        } else if (node instanceof InfixExpression) {
            Object left = eval(((InfixExpression) node).left, env);
            if (isError(left)) {
                return left;
            }

            Object right = eval(((InfixExpression) node).right, env);
            if (isError(right)) {
                return right;
            }
            return evalInfixExpression(((InfixExpression) node).operator, left, right);
        } else if (node instanceof BlockStatement) {
            return evalBlockStatement((BlockStatement) node, env);
        } else if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression) node, env);
        } else if (node instanceof ReturnStatement) {
            Object val = eval(((ReturnStatement) node).returnValue, env);
            if (isError(val)) {
                return val;
            }
            return new ReturnValue(val);
        } else if (node instanceof LetStatement) {
            LetStatement statement = (LetStatement) node;
            Object val = eval(statement.value, env);
            if (isError(val)) {
                return val;
            }

            env.set(statement.name.value, val);
        } else if (node instanceof Identifier) {
            return evalIdentifier((Identifier) node, env);
        } else if (node instanceof FunctionLiteral) {
            List<Identifier> parameters = ((FunctionLiteral) node).parameters;
            BlockStatement body = ((FunctionLiteral) node).body;
            return new Function(parameters, body, env);
        } else if (node instanceof CallExpression) {
            Object function = eval(((CallExpression) node).function, env);
            if (isError(function)) {
                return function;
            }

            List<Object> args = evalExpressions(((CallExpression) node).arguments, env);
            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(0);
            }

            return applyFunction(function, args);
        } else if (node instanceof StringLiteral) {
            return new String(((StringLiteral) node).value);
        }

        return null;
    }

    private Object evalProgram(Program program, Environment env) {
        Object result = null;
        for (Statement statement : program.statements) {
            result = eval(statement, env);
            if (result instanceof ReturnValue) {
                return ((ReturnValue) result).value;
            } else if (result instanceof Error) {
                return result;
            }
        }

        return result;
    }

    private Object evalBlockStatement(BlockStatement block, Environment env) {
        Object result = null;
        for (Statement statement : block.statements) {
            result = eval(statement, env);
            if (result != null) {
                if (ObjectType.RETURN_VALUE_OBJ.equals(result.type()) ||
                        ObjectType.ERROR_OBJ.equals(result.type())) {
                    return result;
                }
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

    private Object evalPrefixExpression(java.lang.String operator, Object right) {
        switch (operator) {
            case "!":
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusPrefixOperatorExpression(right);
            default:
                return new Error("unknown operator: " + operator + right.type());
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
            return new Error("unknown operator: -" + right.type());
        }

        long value = ((Integer) right).value;
        return new Integer(-value);
    }

    private Object evalInfixExpression(java.lang.String operator, Object left, Object right) {
        if (ObjectType.INTEGER_OBJ.equals(left.type()) && ObjectType.INTEGER_OBJ.equals(right.type())) {
            return evalIntegerInfixExpression(operator, left, right);
        } else if (ObjectType.STRING_OBJ.equals(left.type()) && ObjectType.STRING_OBJ.equals(right.type())) {
            return evalStringInfixExpression(operator, left, right);
        } else if ("==".equals(operator)) {
            return nativeBoolToBooleanObject(left == right);
        } else if ("!=".equals(operator)) {
            return nativeBoolToBooleanObject(left != right);
        } else if (!left.type().equals(right.type())) {
            return new Error("type mismatch: " + left.type() + " " + operator + " " + right.type());
        } else {
            return new Error("unknown operator: " + left.type() + " " + operator + " " + right.type());
        }
    }

    private Object evalIntegerInfixExpression(java.lang.String operator, Object left, Object right) {
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
                return new Error("unknown operator: " + left.type() + operator + right.type());
        }
    }

    private Object evalStringInfixExpression(java.lang.String operator, Object left, Object right) {
        if (!"+".equals(operator)) {
            return new Error("unknown operator: " + left.type() + operator + right.type());
        }

        java.lang.String leftValue = ((String) left).value;
        java.lang.String rightValue = ((String) right).value;
        return new String(leftValue + rightValue);
    }

    private Object evalIfExpression(IfExpression ie, Environment env) {
        Object condition = eval(ie.condition, env);
        if (isError(condition)) {
            return condition;
        }

        if (isTruthy(condition)) {
            return eval(ie.consequence, env);
        } else if (ie.alternative != null) {
            return eval(ie.alternative, env);
        } else {
            return NULL;
        }
    }

    private Object evalIdentifier(Identifier node, Environment env) {
        Object val = env.get(node.value);
        if (val == null) {
            return new Error("identifier not found: " + node.value);
        }

        return val;
    }

    private List<Object> evalExpressions(List<Expression> exps, Environment env) {
        List<Object> result = new ArrayList<>();
        for (Expression e : exps) {
            Object evaluated = eval(e, env);
            if (isError(evaluated)) {
                List<Object> errors = new ArrayList<>();
                errors.add(evaluated);
                return errors;
            }
            result.add(evaluated);
        }

        return result;
    }

    private Object applyFunction(Object fn, List<Object> args) {
        if (!(fn instanceof Function)) {
            return new Error("not a function: " + fn.type());
        }

        Function function = (Function) fn;
        Environment extendedEnv = extendFunctionEnv(function, args);
        Object evaluated = eval(function.body, extendedEnv);
        return unwrapReturnValue(evaluated);
    }

    private Environment extendFunctionEnv(Function fn, List<Object> args) {
        Environment env = new Environment(fn.env);
        for (int i = 0; i < fn.parameters.size(); i++) {
            Identifier param = fn.parameters.get(i);
            env.set(param.value, args.get(i));
        }

        return env;
    }

    private Object unwrapReturnValue(Object obj) {
        if (obj instanceof ReturnValue) {
            return ((ReturnValue) obj).value;
        }

        return obj;
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

    private boolean isError(Object obj) {
        if (obj != null) {
            return ObjectType.ERROR_OBJ.equals(obj.type());
        }

        return false;
    }
}
