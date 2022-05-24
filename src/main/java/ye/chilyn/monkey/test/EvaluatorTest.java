package ye.chilyn.monkey.test;

import ye.chilyn.monkey.Lexer;
import ye.chilyn.monkey.Parser;
import ye.chilyn.monkey.Evaluator;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.object.Boolean;
import ye.chilyn.monkey.object.Integer;
import ye.chilyn.monkey.object.Object;

import static ye.chilyn.monkey.Printer.println;

public class EvaluatorTest {
    public void testEvalIntegerExpression() {
        EvalIntegerExpressionTest[] tests = {
                new EvalIntegerExpressionTest("5", 5),
                new EvalIntegerExpressionTest("10", 10),
                new EvalIntegerExpressionTest("-5", -5),
                new EvalIntegerExpressionTest("-10", -10),
        };

        for (EvalIntegerExpressionTest tt : tests) {
            Object evaluated = testEval(tt.input);
            testIntegerObject(evaluated, tt.expected);
        }
        println("success");
    }

    private Object testEval(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        Evaluator evaluator = new Evaluator();
        return evaluator.eval(program);
    }

    private boolean testIntegerObject(Object obj, long expected) {
        if (!(obj instanceof Integer)) {
            println("object is not Integer. got=" +
                    (obj == null ? "null" : obj.getClass().getName()));
            return false;
        }

        long result = ((Integer) obj).value;
        if (result != expected) {
            println("object has wrong value. got=" + result + ", want=" + expected);
            return false;
        }

        return true;
    }

    private class EvalIntegerExpressionTest {
        String input;
        long expected;

        public EvalIntegerExpressionTest(String input, long expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testEvalBooleanExpression() {
        EvalBooleanExpressionTest[] tests = {
                new EvalBooleanExpressionTest("true", true),
                new EvalBooleanExpressionTest("false", false),
        };

        for (EvalBooleanExpressionTest tt : tests) {
            Object evaluated = testEval(tt.input);
            testBooleanObject(evaluated, tt.expected);
        }
        println("success");
    }

    private boolean testBooleanObject(Object obj, boolean expected) {
        if (!(obj instanceof Boolean)) {
            println("object is not Boolean. got=" +
                    (obj == null ? "null" : obj.getClass().getName()));
            return false;
        }

        boolean result = ((Boolean) obj).value;
        if (result != expected) {
            println("object has wrong value. got=" + result + ", want=" + expected);
            return false;
        }

        return true;
    }

    private class EvalBooleanExpressionTest {
        String input;
        boolean expected;

        public EvalBooleanExpressionTest(String input, boolean expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testBangOperator() {
        BangOperatorTest[] tests = {
                new BangOperatorTest("!true", false),
                new BangOperatorTest("!false", true),
                new BangOperatorTest("!5", false),
                new BangOperatorTest("!!true", true),
                new BangOperatorTest("!!false", false),
                new BangOperatorTest("!!5", true),
        };

        for (BangOperatorTest tt : tests) {
            Object evaluated = testEval(tt.input);
            testBooleanObject(evaluated, tt.expected);
        }
        println("success");
    }

    private class BangOperatorTest {
        String input;
        boolean expected;

        public BangOperatorTest(String input, boolean expected) {
            this.input = input;
            this.expected = expected;
        }
    }
}
