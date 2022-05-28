package ye.chilyn.monkey.test;

import java.lang.String;

import ye.chilyn.monkey.Lexer;
import ye.chilyn.monkey.Parser;
import ye.chilyn.monkey.Evaluator;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.object.Boolean;
import ye.chilyn.monkey.object.Environment;
import ye.chilyn.monkey.object.Error;
import ye.chilyn.monkey.object.Function;
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
                new EvalIntegerExpressionTest("5 + 5 + 5 + 5 - 10", 10),
                new EvalIntegerExpressionTest("2 * 2 * 2 * 2 * 2", 32),
                new EvalIntegerExpressionTest("-50 + 100 + -50", 0),
                new EvalIntegerExpressionTest("5 * 2 + 10", 20),
                new EvalIntegerExpressionTest("5 + 2 * 10", 25),
                new EvalIntegerExpressionTest("20 + 2 * -10", 0),
                new EvalIntegerExpressionTest("50 / 2 * 2 + 10", 60),
                new EvalIntegerExpressionTest("2 * (5 + 10)", 30),
                new EvalIntegerExpressionTest("3 * 3 * 3 + 10", 37),
                new EvalIntegerExpressionTest("3 * (3 * 3) + 10", 37),
                new EvalIntegerExpressionTest("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50),
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
        Environment env = new Environment();
        return evaluator.eval(program, env);
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
                new EvalBooleanExpressionTest("false", false),
                new EvalBooleanExpressionTest("1 < 2", true),
                new EvalBooleanExpressionTest("1 > 2", false),
                new EvalBooleanExpressionTest("1 < 1", false),
                new EvalBooleanExpressionTest("1 > 1", false),
                new EvalBooleanExpressionTest("1 == 1", true),
                new EvalBooleanExpressionTest("1 != 1", false),
                new EvalBooleanExpressionTest("1 == 2", false),
                new EvalBooleanExpressionTest("1 != 2", true),
                new EvalBooleanExpressionTest("true == true", true),
                new EvalBooleanExpressionTest("false == false", true),
                new EvalBooleanExpressionTest("true == false", false),
                new EvalBooleanExpressionTest("true != false", true),
                new EvalBooleanExpressionTest("false != true", true),
                new EvalBooleanExpressionTest("(1 < 2) == true", true),
                new EvalBooleanExpressionTest("(1 < 2) == false", false),
                new EvalBooleanExpressionTest("(1 > 2) == true", false),
                new EvalBooleanExpressionTest("(1 > 2) == false", true),
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

    public void testIfElseExpressions() {
        IfElseExpressionsTest[] tests = {
                new IfElseExpressionsTest("if (true) { 10 }", 10),
                new IfElseExpressionsTest("if (false) { 10 }", null),
                new IfElseExpressionsTest("if (1) { 10 }", 10),
                new IfElseExpressionsTest("if (1 < 2) { 10 }", 10),
                new IfElseExpressionsTest("if (1 > 2) { 10 }", null),
                new IfElseExpressionsTest("if (1 > 2) { 10 } else { 20 }", 20),
                new IfElseExpressionsTest("if (1 < 2) { 10 } else { 20 }", 10),
        };

        for (IfElseExpressionsTest tt : tests) {
            Object evaluated = testEval(tt.input);
            if (tt.expected instanceof java.lang.Integer) {
                testIntegerObject(evaluated, (java.lang.Integer) tt.expected);
            } else {
                testNullObject(evaluated);
            }
        }
        println("success");
    }

    private boolean testNullObject(Object obj) {
        if (obj != Evaluator.NULL) {
            println("object is not NULL. got=" +
                    (obj == null ? "null" : obj.getClass().getName()));
            return false;
        }

        return true;
    }

    private class IfElseExpressionsTest {
        String input;
        java.lang.Object expected;

        public IfElseExpressionsTest(String input, java.lang.Object expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testReturnStatements() {
        ReturnStatementsTest[] tests = {
                new ReturnStatementsTest("return 10;", 10),
                new ReturnStatementsTest("return 10; 9;", 10),
                new ReturnStatementsTest("return 2 * 5; 9;", 10),
                new ReturnStatementsTest("9; return 2 * 5; 9;", 10),
                new ReturnStatementsTest("if (10 > 1) { if (10 > 1) { return 10; }return 1; }", 10),
        };

        for (ReturnStatementsTest tt : tests) {
            Object evaluated = testEval(tt.input);
            testIntegerObject(evaluated, tt.expected);
        }
        println("success");
    }

    private class ReturnStatementsTest {
        String input;
        long expected;

        public ReturnStatementsTest(String input, long expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testErrorHandling() {
        ErrorHandlingTest[] tests = {
//                new ErrorHandlingTest("foobar", "identifier not found: foobar"),
//                new ErrorHandlingTest("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
//                new ErrorHandlingTest("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
//                new ErrorHandlingTest("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
//                new ErrorHandlingTest("-true", "unknown operator: -BOOLEAN"),
//                new ErrorHandlingTest("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
//                new ErrorHandlingTest("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"),
//                new ErrorHandlingTest("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
//                new ErrorHandlingTest("if (10 > 1) { if (10 > 1) { return true + false; }return 1; }", "unknown operator: BOOLEAN + BOOLEAN"),
                new ErrorHandlingTest("\"Hello\" - \"World\"", "unknown operator: STRING - STRING"),
        };

        for (ErrorHandlingTest tt : tests) {
            Object evaluated = testEval(tt.input);
            if (!(evaluated instanceof Error)) {
                StringBuilder sb = new StringBuilder("no error object returned. got=");
                if (evaluated == null) {
                    sb.append("null");
                } else {
                    sb.append(evaluated.getClass().getName());
                    sb.append("(");
                    sb.append(evaluated.inspect());
                    sb.append(")");
                }
                println(sb.toString());
                continue;
            }

            Error error = (Error) evaluated;
            if (!tt.expectedMessage.equals(error.message)) {
                println("wrong error message. expected=" + tt.expectedMessage + ", got=" + error.message);
            }
        }
        println("success");
    }

    private class ErrorHandlingTest {
        String input;
        String expectedMessage;

        public ErrorHandlingTest(String input, String expectedMessage) {
            this.input = input;
            this.expectedMessage = expectedMessage;
        }
    }

    public void testLetStatements() {
        LetStatementsTest[] tests = {
                new LetStatementsTest("let a = 5; a;", 5),
                new LetStatementsTest("let a = 5 * 5; a;", 25),
                new LetStatementsTest("let a = 5; let b = a; b;", 5),
                new LetStatementsTest("let a = 5; let b = a; let c = a + b + 5; c;", 15),
        };

        for (LetStatementsTest tt : tests) {
            testIntegerObject(testEval(tt.input), tt.expected);
        }

        println("success");
    }

    private class LetStatementsTest {
        String input;
        long expected;

        public LetStatementsTest(String input, long expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testFunctionObject() {
        String input = "fn(x) { x + 2; };";
        Object evaluated = testEval(input);
        if (!(evaluated instanceof Function)) {
            StringBuilder sb = new StringBuilder("object is not Function. got=");
            if (evaluated == null) {
                sb.append("null");
            } else {
                sb.append(evaluated.getClass().getName());
                sb.append("(");
                sb.append(evaluated.inspect());
                sb.append(")");
            }
            println(sb.toString());
            return;
        }

        Function fn = (Function) evaluated;
        if (fn.parameters.size() != 1) {
            println("function has wrong parameters. Parameters=" + fn.parameters.toString());
            return;
        }

        if (!"x".equals(fn.parameters.get(0).string())) {
            println("parameter is not 'x'. got=" + fn.parameters.get(0).string());
            return;
        }

        String expectedBody = "(x + 2)";
        if (!expectedBody.equals(fn.body.string())) {
            println("body is not " + expectedBody + ". got=" + fn.body.string());
            return;
        }

        println("success");
    }

    public void testFunctionApplication() {
        FunctionApplicationTest[] tests = {
                new FunctionApplicationTest("let identity = fn(x) { x; }; identity(5);", 5),
                new FunctionApplicationTest("let identity = fn(x) { return x; }; identity(5);", 5),
                new FunctionApplicationTest("let double = fn(x) { x * 2; }; double(5);", 10),
                new FunctionApplicationTest("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
                new FunctionApplicationTest("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
                new FunctionApplicationTest("fn(x) { x; }(5)", 5),
        };

        for (FunctionApplicationTest tt : tests) {
            testIntegerObject(testEval(tt.input), tt.expected);
        }

        println("success");
    }

    private class FunctionApplicationTest {
        String input;
        long expected;

        public FunctionApplicationTest(String input, long expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testClosures() {
        String input = "let newAdder = fn(x) { fn(y) { x + y }; };let addTwo = newAdder(2); addTwo(2);";
        if (!testIntegerObject(testEval(input), 4)) {
            return;
        }
        println("success");
    }

    public void testStringLiteral() {
        String input = "\"Hello World!\"";
        Object evaluated = testEval(input);
        if (!(evaluated instanceof ye.chilyn.monkey.object.String)) {
            println("object is not String. got=" + evaluated.type() + "(" + evaluated.inspect() + ")");
        }

        ye.chilyn.monkey.object.String str = (ye.chilyn.monkey.object.String) evaluated;
        if (!"Hello World!".equals(str.value)) {
            println("String has wrong value. got=" + str.value);
            return;
        }
        println("success");
    }

    public void testStringConcatenation() {
        String input = "\"Hello\" + \" \" + \"World!\"";
        Object evaluated = testEval(input);
        if (!(evaluated instanceof ye.chilyn.monkey.object.String)) {
            println("object is not String. got=" + evaluated.type() + "(" + evaluated.inspect() + ")");
            return;
        }

        ye.chilyn.monkey.object.String str = (ye.chilyn.monkey.object.String) evaluated;
        if (!"Hello World!".equals(str.value)) {
            println("String has wrong value. got=" + str.value);
            return;
        }

        println("success");
    }
}
