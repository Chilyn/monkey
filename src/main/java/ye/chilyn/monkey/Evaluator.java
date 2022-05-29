package ye.chilyn.monkey;

import static ye.chilyn.monkey.Printer.println;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import ye.chilyn.monkey.ast.ArrayLiteral;
import ye.chilyn.monkey.ast.BlockStatement;
import ye.chilyn.monkey.ast.CallExpression;
import ye.chilyn.monkey.ast.Expression;
import ye.chilyn.monkey.ast.ExpressionStatement;
import ye.chilyn.monkey.ast.FunctionLiteral;
import ye.chilyn.monkey.ast.HashLiteral;
import ye.chilyn.monkey.ast.Identifier;
import ye.chilyn.monkey.ast.IfExpression;
import ye.chilyn.monkey.ast.IndexExpression;
import ye.chilyn.monkey.ast.InfixExpression;
import ye.chilyn.monkey.ast.IntegerLiteral;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.Node;
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.ReturnStatement;
import ye.chilyn.monkey.ast.Statement;
import ye.chilyn.monkey.ast.StringLiteral;
import ye.chilyn.monkey.object.Array;
import ye.chilyn.monkey.object.Boolean;
import ye.chilyn.monkey.object.Builtin;
import ye.chilyn.monkey.object.BuiltinFunction;
import ye.chilyn.monkey.object.Environment;
import ye.chilyn.monkey.object.Error;
import ye.chilyn.monkey.object.Function;
import ye.chilyn.monkey.object.Hash;
import ye.chilyn.monkey.object.HashKey;
import ye.chilyn.monkey.object.HashPair;
import ye.chilyn.monkey.object.HashTable;
import ye.chilyn.monkey.object.Integer;
import ye.chilyn.monkey.object.Null;
import ye.chilyn.monkey.object.Object;
import ye.chilyn.monkey.object.ObjectType;
import ye.chilyn.monkey.object.ReturnValue;
import ye.chilyn.monkey.object.String;

public class Evaluator {
    public static final Null NULL = new Null();
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);
    private Map<java.lang.String, Builtin> builtins = new HashMap<java.lang.String, Builtin>(){{
        put("len", new Builtin(new BuiltinFunction() {
            @Override
            public Object builtinFunction(Object... args) {
                if (args.length != 1) {
                    return new Error("wrong number of arguments. got=" + args.length + ", want=1");
                }

                if (args[0] instanceof String) {
                    return new Integer(((String) args[0]).value.length());
                }

                if (args[0] instanceof Array) {
                    return new Integer(((Array) args[0]).elements.size());
                }


                return new Error("argument to `len` not supported, got " + args[0].type());
            }
        }));
        put("first", new Builtin(new BuiltinFunction() {
            @Override
            public Object builtinFunction(Object... args) {
                if (args.length != 1 || !(args[0] instanceof Array)) {
                    return new Error("argument to `first` must be ARRAY, got " + args[0].type());
                }

                Array arr = (Array) args[0];
                if (arr.elements.size() > 0) {
                    return arr.elements.get(0);
                }

                return NULL;
            }
        }));
        put("last", new Builtin(new BuiltinFunction() {
            @Override
            public Object builtinFunction(Object... args) {
                if (args.length != 1 || !(args[0] instanceof Array)) {
                    return new Error("argument to `last` must be ARRAY, got " + args[0].type());
                }

                Array arr = (Array) args[0];
                int length = arr.elements.size();
                if (length > 0) {
                    return arr.elements.get(length - 1);
                }

                return NULL;
            }
        }));
        put("rest", new Builtin(new BuiltinFunction() {
            @Override
            public Object builtinFunction(Object... args) {
                if (args.length != 1 || !(args[0] instanceof Array)) {
                    return new Error("argument to `rest` must be ARRAY, got " + args[0].type());
                }

                Array arr = (Array) args[0];
                int length = arr.elements.size();
                if (length > 0) {
                    List<Object> newElements = new ArrayList<>(arr.elements);
                    newElements.remove(0);
                    return new Array(newElements);
                }

                return NULL;
            }
        }));
        put("push", new Builtin(new BuiltinFunction() {
            @Override
            public Object builtinFunction(Object... args) {
                if (args.length != 2 || !(args[0] instanceof Array)) {
                    return new Error("argument to `push` must be ARRAY, got " + args[0].type());
                }

                Array arr = (Array) args[0];
                List<Object> newElements = new ArrayList<>(arr.elements);
                newElements.add(args[1]);
                return new Array(newElements);
            }
        }));
        put("puts", new Builtin(new BuiltinFunction() {
            @Override
            public Object builtinFunction(Object... args) {
                for (Object arg : args) {
                    println(arg.inspect());
                }

                return NULL;
            }
        }));
    }};

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
        } else if (node instanceof ArrayLiteral) {
            List<Object> elements = evalExpressions(((ArrayLiteral) node).elements, env);
            if (elements.size() == 1 && isError(elements.get(0))) {
                return elements.get(0);
            }

            return new Array(elements);
        } else if (node instanceof IndexExpression) {
            Object left = eval(((IndexExpression) node).left, env);
            if (isError(left)) {
                return left;
            }

            Object index = eval(((IndexExpression) node).index, env);
            if (isError(index)) {
                return index;
            }
            return evalIndexExpression(left, index);
        } else if (node instanceof HashLiteral) {
            return evalHashLiteral((HashLiteral) node, env);
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
        java.lang.String leftType = left == null ? null: left.type();
        java.lang.String rightType = right == null ? null: right.type();
        if (ObjectType.INTEGER_OBJ.equals(leftType) && ObjectType.INTEGER_OBJ.equals(rightType)) {
            return evalIntegerInfixExpression(operator, left, right);
        } else if (ObjectType.STRING_OBJ.equals(leftType) && ObjectType.STRING_OBJ.equals(rightType)) {
            return evalStringInfixExpression(operator, left, right);
        } else if ("==".equals(operator)) {
            return nativeBoolToBooleanObject(left == right);
        } else if ("!=".equals(operator)) {
            return nativeBoolToBooleanObject(left != right);
        } else if (left != null && !left.type().equals(rightType)) {
            return new Error("type mismatch: " + left.type() + " " + operator + " " + rightType);
        } else {
            return new Error("unknown operator: " + leftType + " " + operator + " " + rightType);
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
        if (val != null) {
            return val;
        }

        Builtin builtin = builtins.get(node.value);
        if (builtin != null) {
            return builtin;
        }

        return new Error("identifier not found: " + node.value);
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
        if (fn instanceof Function) {
            Function function = (Function) fn;
            Environment extendedEnv = extendFunctionEnv(function, args);
            Object evaluated = eval(function.body, extendedEnv);
            return unwrapReturnValue(evaluated);
        }

        if (fn instanceof Builtin) {
            return ((Builtin) fn).fn.builtinFunction(args.toArray(new Object[0]));
        }

        return new Error("not a function: " + fn.type());
    }

    private Environment extendFunctionEnv(Function fn, List<Object> args) {
        Environment env = new Environment(fn.env);
        for (int i = 0; i < fn.parameters.size(); i++) {
            Identifier param = fn.parameters.get(i);
            env.set(param.value, args.get(i));
        }

        return env;
    }

    private Object evalIndexExpression(Object left, Object index) {
        if (ObjectType.ARRAY_OBJ.equals(left.type()) &&
            ObjectType.INTEGER_OBJ.equals(index.type())) {
            return evalArrayIndexExpression(left, index);
        }

        if (ObjectType.HASH_OBJ.equals(left.type())) {
            return evalHashIndexExpression(left, index);
        }

        return new Error("index operator not supported: " + left.type());
    }

    private Object evalArrayIndexExpression(Object array, Object index) {
        Array arrayObject = (Array) array;
        int idx = (int) ((Integer) index).value;
        int max = arrayObject.elements.size() - 1;
        if (idx < 0 || idx > max) {
            return NULL;
        }

        return arrayObject.elements.get(idx);
    }

    private Object evalHashLiteral(HashLiteral node, Environment env) {
        Map<HashKey, HashPair> pairs = new HashMap<>();
        for (Map.Entry<Expression, Expression> entry : node.pairs.entrySet()) {
            Expression keyNode = entry.getKey();
            Expression valueNode = entry.getValue();
            Object key = eval(keyNode, env);
            if (isError(key)) {
                return key;
            }

            if (!(key instanceof HashTable)) {
                return new Error("unusable as hash key: " + key.type());
            }

            Object value = eval(valueNode, env);
            if (isError(value)) {
                return value;
            }

            HashKey hashed = ((HashTable) key).hashKey();
            pairs.put(hashed, new HashPair(key, value));
        }

        return new Hash(pairs);
    }

    private Object evalHashIndexExpression(Object hash, Object index) {
        Hash hashObject = (Hash) hash;
        if (!(index instanceof HashTable)) {
            return new Error("unusable as hash key: " + index.type());
        }

        HashKey key = ((HashTable) index).hashKey();
        HashPair pair = hashObject.pairs.get(key);
        if (pair == null) {
            return NULL;
        }

        return pair.value;
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
