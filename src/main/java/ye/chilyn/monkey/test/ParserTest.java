package ye.chilyn.monkey.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ye.chilyn.monkey.Printer.print;
import static ye.chilyn.monkey.Printer.println;

import ye.chilyn.monkey.Lexer;
import ye.chilyn.monkey.Parser;
import ye.chilyn.monkey.ast.ArrayLiteral;
import ye.chilyn.monkey.ast.Boolean;
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
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.ReturnStatement;
import ye.chilyn.monkey.ast.Statement;
import ye.chilyn.monkey.ast.StringLiteral;

public class ParserTest {

    public void testLetStatements() {
        LetStatementTest[] tests = {
                new LetStatementTest("let x = 5;", "x", 5),
                new LetStatementTest("let y = true;", "y", true),
                new LetStatementTest("let foobar = y", "foobar", "y"),
        };

        for (LetStatementTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);
            int len = program.statements.size();
            if (len != 1) {
                println("program.statements does not contain 1 statements. got=" + len);
                return;
            }

            Statement statement = program.statements.get(0);
            if (!testLetStatement(statement, tt.expectedIdentifier)) {
                return;
            }

            Expression val = ((LetStatement) statement).value;
            if (!testLiteralExpression(val, tt.expectedValue)) {
                return;
            }
        }
        println("success");
    }

    private class LetStatementTest {
        String input;
        String expectedIdentifier;
        Object expectedValue;

        public LetStatementTest(String input, String expectedIdentifier, Object expectedValue) {
            this.input = input;
            this.expectedIdentifier = expectedIdentifier;
            this.expectedValue = expectedValue;
        }
    }

    private void checkParserErrors(Parser parser) {
        List<String> errors = parser.errors();
        if (errors.size() == 0) {
            return;
        }

        println("parser has " + errors.size() + " errors");
        for (String error : errors) {
            println(error);
        }

//        System.exit(0);
    }

    public boolean testLetStatement(Statement s, String name) {
        if (!"let".equals(s.tokenLiteral())) {
            println("s.tokenLiteral not 'let'. got=" + s.tokenLiteral());
            return false;
        }

        if (!(s instanceof LetStatement)) {
            println("s not LetStatement. got=" + s.getClass().getName());
            return false;
        }

        LetStatement letStatement = (LetStatement) s;
        if (!name.equals(letStatement.name.value)) {
            println("letStatement.name.value not " + name + ". got=" + letStatement.name.value);
            return false;
        }

        if (!name.equals(letStatement.name.tokenLiteral())) {
            println("s.name not " + name + ". got=" + letStatement.name.tokenLiteral());
            return false;
        }

        return true;
    }

    public void testReturnStatements() {
        String input = "return 5;return 10;return 993322;";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        int len = program.statements.size();
        if (len != 3) {
            println("program.statements does not contain 3 statements. got=" + len);
            return;
        }

        for (Statement statement : program.statements) {
            if (!(statement instanceof ReturnStatement)) {
                println("statement not ReturnStatement. got=" + statement.getClass().getName());
                continue;
            }

            ReturnStatement returnStmt = (ReturnStatement) statement;
            if (!"return".equals(returnStmt.tokenLiteral())) {
                println("returnStmt.tokenLiteral not 'return'. got=" + returnStmt.tokenLiteral());
            }
        }
        println("success");
    }

    public void testIdentifierExpression() {
        String input = "foobar;";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        if (program.statements.size() != 1) {
            println("program has not enough statements. got=" +
                    program.statements.size());
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof Identifier)) {
            println("expected not Identifier. got=" +
                    expression.getClass().getName());
            return;
        }

        Identifier ident = (Identifier) expression;
        if (!"foobar".equals(ident.value)) {
            println("ident.Value not foobar. got=" + ident.value);
            return;
        }

        if (!"foobar".equals(ident.tokenLiteral())) {
            println("ident.tokenLiteral not foobar. got=" + ident.tokenLiteral());
            return;
        }

        println("success");
    }

    public void testIntegerLiteralExpression() {
        String input = "5;";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        if (program.statements.size() != 1) {
            println("program has not enough statements. got=" +
                    program.statements.size());
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof IntegerLiteral)) {
            println("expression not IntegerLiteral. got=" +
                    (expression == null ? "null" : expression.getClass().getName()));
            return;
        }

        IntegerLiteral literal = (IntegerLiteral) expression;
        if (literal.value != 5) {
            println("literal.Value not 5. got=" + literal.value);
            return;
        }

        if (!"5".equals(literal.tokenLiteral())) {
            println("literal.tokenLiteral not 5. got=" + literal.tokenLiteral());
            return;
        }

        println("success");
    }

    public void testParsingPrefixExpressions() {
        PrefixTest[] tests = {
                new PrefixTest("!true;", "!", true),
                new PrefixTest("!false;", "!", false),
                new PrefixTest("!5;", "!", 5),
                new PrefixTest("-15;", "-", 15),
        };

        for (PrefixTest t : tests) {
            Lexer lexer = new Lexer(t.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);
            if (program.statements.size() != 1) {
                println("program.Statements does not contain 1 statements. got=" +
                        program.statements.size());
                continue;
            }

            Statement statement = program.statements.get(0);
            if (!(statement instanceof ExpressionStatement)) {
                println("program.Statements[0] is not ExpressionStatement. got=" +
                        statement.getClass().getName());
                continue;
            }

            Expression expression = ((ExpressionStatement)statement).expression;
            testPrefixExpression(expression, t.operator, t.value);

        }
        println("success");
    }

    private boolean testPrefixExpression(
            Expression exp,
            String operator,
            Object right
    ) {
        if (!(exp instanceof PrefixExpression)) {
            println("exp is not PrefixExpression. got=" +
                    (exp == null ? "null" : exp.getClass().getName()));
            return false;
        }

        PrefixExpression opExp = (PrefixExpression) exp;
        if (!operator.equals(opExp.operator)) {
            println("exp.operator is not '" + operator + "'. got=" + opExp.operator);
            return false;
        }

        if (!testLiteralExpression(opExp.right, right)) {
            return false;
        }

        return true;
    }

    private class PrefixTest {
        String input;
        String operator;
        Object value;

        public PrefixTest(String input, String operator, Object value) {
            this.input = input;
            this.operator = operator;
            this.value = value;
        }
    }

    public void testParsingInfixExpressions() {
        InfixTest[] infixTests = {
                new InfixTest("true == true", true, "==", true),
                new InfixTest("true != false", true, "!=", false),
                new InfixTest("false == false", false, "==", false),
                new InfixTest("5 + 5;", 5, "+", 5),
                new InfixTest("5 - 5;", 5, "-", 5),
                new InfixTest("5 * 5;", 5, "*", 5),
                new InfixTest("5 / 5;", 5, "/", 5),
                new InfixTest("5 > 5;", 5, ">", 5),
                new InfixTest("5 < 5;", 5, "<", 5),
                new InfixTest("5 == 5;", 5, "==", 5),
                new InfixTest("5 != 5;", 5, "!=", 5),
        };

        for (InfixTest t : infixTests) {
            Lexer lexer = new Lexer(t.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);
            if (program.statements.size() != 1) {
                println("program.Statements does not contain 1 statements. got=" +
                        program.statements.size());
                continue;
            }

            Statement statement = program.statements.get(0);
            if (!(statement instanceof ExpressionStatement)) {
                println("program.Statements[0] is not ExpressionStatement. got=" +
                        statement.getClass().getName());
                continue;
            }

            Expression expression = ((ExpressionStatement)statement).expression;
            testInfixExpression(expression, t.leftValue, t.operator, t.rightValue);
        }
        println("success");
    }

    private boolean testIntegerLiteral(Expression right , long value) {
        if (!(right instanceof IntegerLiteral)) {
            println("right not IntegerLiteral. got=" + (right == null ? "null" : right.getClass().getName()));
            return false;
        }

        IntegerLiteral integ = (IntegerLiteral) right;
        if (integ.value != value) {
            println("integ.value not " + value + ". got=" + integ.value);
            return false;
        }

        if (!String.valueOf(value).equals(integ.tokenLiteral())) {
            println("integ.tokenLiteral not " + value + ". got=" + integ.tokenLiteral());
            return false;
        }

        return true;
    }

    private boolean testIdentifier(Expression exp, String value) {
        if (!(exp instanceof Identifier)) {
            println("exp not Identifier. got=" +
                    (exp == null ? "null" : exp.getClass().getName()));
            return false;
        }

        Identifier ident = (Identifier) exp;
        if (!value.equals(ident.value)) {
            println("ident.value not " + value + ". got=" + ident.value);
            return false;
        }

        if (!value.equals(ident.tokenLiteral())) {
            println("ident.tokenLiteral not " + value + ". got=" + ident.tokenLiteral());
            return false;
        }

        return true;
    }

    private boolean testBooleanLiteral(Expression exp, boolean value) {
        if (!(exp instanceof Boolean)) {
            println("exp not Boolean. got=" +
                    (exp == null ? "null" : exp.getClass().getName()));
            return false;
        }

        Boolean bo = (Boolean) exp;
        if (bo.value != value) {
            println("bo.value not " + value + ". got=" + bo.value);
            return false;
        }

        if (!String.valueOf(value).equals(bo.tokenLiteral())) {
            println("bo.tokenLiteral not " + value + ". got=" + bo.tokenLiteral());
            return false;
        }

        return true;
    }

    private boolean testLiteralExpression(Expression exp, Object expected) {
        if (expected instanceof Integer) {
            return testIntegerLiteral(exp, (int) expected);
        } else if ((expected instanceof Long)) {
            return testIntegerLiteral(exp, (long) expected);
        } else if (expected instanceof String) {
            return testIdentifier(exp, (String) expected);
        } else if (expected instanceof java.lang.Boolean) {
            return testBooleanLiteral(exp, (java.lang.Boolean) expected);
        } else {
            println("type of exp not handled. got=" +
                    (exp == null ? "null" : exp.getClass().getName()));
            return false;
        }
    }

    private boolean testInfixExpression(
            Expression exp,
            Object left,
            String operator,
            Object right
    ) {
        if (!(exp instanceof InfixExpression)) {
            println("exp is not InfixExpression. got=" +
                    (exp == null ? "null" : exp.getClass().getName()));
            return false;
        }

        InfixExpression opExp = (InfixExpression) exp;
        if (!testLiteralExpression(opExp.left, left)) {
            return false;
        }

        if (!operator.equals(opExp.operator)) {
            println("exp.operator is not '" + operator + "'. got=" + opExp.operator);
            return false;
        }

        if (!testLiteralExpression(opExp.right, right)) {
            return false;
        }

        return true;
    }

    private class InfixTest {
        String input;
        Object leftValue;
        String operator;
        Object rightValue;

        public InfixTest(String input, Object leftValue, String operator, Object rightValue) {
            this.input = input;
            this.leftValue = leftValue;
            this.operator = operator;
            this.rightValue = rightValue;
        }
    }

    public void testOperatorPrecedenceParsing() {
        OperatorPrecedenceTest[] tests = {
                new OperatorPrecedenceTest("a * [1, 2, 3, 4][b * c] * d", "((a * ([1, 2, 3, 4][(b * c)])) * d)"),
                new OperatorPrecedenceTest("add(a * b[2], b[1], 2 * [1, 2][1])", "add((a * (b[2])), (b[1]), (2 * ([1, 2][1])))"),
                new OperatorPrecedenceTest("a + add(b * c) + d", "((a + add((b * c))) + d)"),
                new OperatorPrecedenceTest("add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))", "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))"),
                new OperatorPrecedenceTest("add(a + b + c * d / f + g)", "add((((a + b) + ((c * d) / f)) + g))"),
                new OperatorPrecedenceTest("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
                new OperatorPrecedenceTest("(5 + 5) * 2", "((5 + 5) * 2)"),
                new OperatorPrecedenceTest("2 / (5 + 5)", "(2 / (5 + 5))"),
                new OperatorPrecedenceTest("-(5 + 5)", "(-(5 + 5))"),
                new OperatorPrecedenceTest("!(true == true)", "(!(true == true))"),
                new OperatorPrecedenceTest("true", "true"),
                new OperatorPrecedenceTest("false", "false"),
                new OperatorPrecedenceTest("3 > 5 == false", "((3 > 5) == false)"),
                new OperatorPrecedenceTest("3 < 5 == true", "((3 < 5) == true)"),
                new OperatorPrecedenceTest("-a * b", "((-a) * b)"),
                new OperatorPrecedenceTest("!-a", "(!(-a))"),
                new OperatorPrecedenceTest("a + b + c", "((a + b) + c)"),
                new OperatorPrecedenceTest("a + b - c", "((a + b) - c)"),
                new OperatorPrecedenceTest("a * b * c", "((a * b) * c)"),
                new OperatorPrecedenceTest("a * b / c", "((a * b) / c)"),
                new OperatorPrecedenceTest("a + b / c", "(a + (b / c))"),
                new OperatorPrecedenceTest("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
                new OperatorPrecedenceTest("3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"),
                new OperatorPrecedenceTest("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
                new OperatorPrecedenceTest("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
                new OperatorPrecedenceTest("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
        };

        for (OperatorPrecedenceTest t : tests) {
            Lexer lexer = new Lexer(t.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);
            String actual = program.string();
            if (!t.expected.equals(actual)) {
                println("expected=" + t.expected + ", got=" + actual);
            }
        }
        println("success");
    }

    private class OperatorPrecedenceTest {
        String input;
        String expected;

        public OperatorPrecedenceTest(String input, String expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public void testBooleanExpression() {
        String input = "true; false; let foobar = true; let barfoo = false;";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        if (program.statements.size() != 4) {
            println("program has not enough statements. got=" +
                    program.statements.size());
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof Boolean)) {
            println("expression not Boolean. got=" +
                    (expression == null ? "null" : expression.getClass().getName()));
            return;
        }

        Boolean b = (Boolean) expression;
        if (!("true".equals(b.tokenLiteral()) || "false".equals(b.tokenLiteral()))) {
            println("literal.tokenLiteral not 'true' or 'false'. got=" + b.tokenLiteral());
            return;
        }

        println("success");
    }

    public void testIfExpression() {
        String input = "if (x < y) { x }";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        int len = program.statements.size();
        if (len != 1) {
            println("program.statements does not contain 1 statements. got=" + len);
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof IfExpression)) {
            println("expression not IfExpression. got=" +
                    (expression == null ? "null" : expression.getClass().getName()));
            return;
        }

        IfExpression exp = (IfExpression) expression;
        if (!testInfixExpression(exp.condition, "x", "<", "y")) {
            return;
        }

        if (exp.consequence.statements.size() != 1) {
            println("consequence is not 1 statements. got=" + exp.consequence.statements.size());
            return;
        }

        Statement conseSmtt = exp.consequence.statements.get(0);
        if (!(conseSmtt instanceof ExpressionStatement)) {
            println("consequence.tatements[0] is not ExpressionStatement. got=" +
                    (conseSmtt == null ? "null" : conseSmtt.getClass().getName()));
            return;
        }

        ExpressionStatement consequence = (ExpressionStatement) conseSmtt;
        if (!testIdentifier(consequence.expression, "x")) {
            return;
        }

        if (exp.alternative != null) {
            println("exp.Alternative.Statements was not null. got=" +
                    exp.alternative.tokenLiteral());
            return;
        }

        println("success");
    }

    public void testIfElseExpression() {
        String input = "if (x < y) { x } else { y }";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        int len = program.statements.size();
        if (len != 1) {
            println("program.statements does not contain 1 statements. got=" + len);
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof IfExpression)) {
            println("expression not IfExpression. got=" +
                    (expression == null ? "null" : expression.getClass().getName()));
            return;
        }

        IfExpression exp = (IfExpression) expression;
        if (!testInfixExpression(exp.condition, "x", "<", "y")) {
            return;
        }

        if (exp.consequence.statements.size() != 1) {
            println("consequence is not 1 statements. got=" + exp.consequence.statements.size());
            return;
        }

        Statement conseSmtt = exp.consequence.statements.get(0);
        if (!(conseSmtt instanceof ExpressionStatement)) {
            println("consequence.tatements[0] is not ExpressionStatement. got=" +
                    (conseSmtt == null ? "null" : conseSmtt.getClass().getName()));
            return;
        }

        ExpressionStatement consequence = (ExpressionStatement) conseSmtt;
        if (!testIdentifier(consequence.expression, "x")) {
            return;
        }

        if (exp.alternative == null) {
            println("exp.Alternative.Statements was  null");
        }

        if (exp.alternative.statements.size() != 1) {
            println("consequence is not 1 statements. got=" + exp.alternative.statements.size());
            return;
        }

        Statement alterSmtt = exp.alternative.statements.get(0);
        if (!(alterSmtt instanceof ExpressionStatement)) {
            println("alterSmtt.tatements[0] is not ExpressionStatement. got=" +
                    (alterSmtt == null ? "null" : alterSmtt.getClass().getName()));
            return;
        }

        ExpressionStatement alternative = (ExpressionStatement) alterSmtt;
        if (!testIdentifier(alternative.expression, "y")) {
            return;
        }

        println("success");
    }

    public void testFunctionLiteralParsing() {
        String input = "fn(x, y) { x + y; }";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        int len = program.statements.size();
        if (len != 1) {
            println("program.statements does not contain 1 statements. got=" + len);
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof FunctionLiteral)) {
            println("expression not FunctionLiteral. got=" +
                    (expression == null ? "null" : expression.getClass().getName()));
            return;
        }

        FunctionLiteral function = (FunctionLiteral) expression;
        if (function.parameters.size() != 2) {
            println("function literal parameters wrong. want 2, got=" + function.parameters.size());
            return;
        }

        testLiteralExpression(function.parameters.get(0), "x");
        testLiteralExpression(function.parameters.get(1), "y");

        int bodyStatementsSize = function.body.statements.size();
        if (bodyStatementsSize != 1) {
            println("function.body.statements has not 1 statements. got=" + bodyStatementsSize);
            return;
        }

        Statement bodyStmt = function.body.statements.get(0);
        if (!(bodyStmt instanceof ExpressionStatement)) {
            println("function.body.statements[0] is not ExpressionStatement. got=" +
                    (bodyStmt == null ? "null" : bodyStmt.getClass().getName()));
            return;
        }

        if (!testInfixExpression(((ExpressionStatement) bodyStmt).expression, "x", "+", "y")) {
            return;
        }
        println("success");
    }

    public void testFunctionParameterParsing() {
        FunctionParameterTest[] tests = {
                new FunctionParameterTest("fn() {};", new String[]{}),
                new FunctionParameterTest("fn(x) {};", new String[]{"x"}),
                new FunctionParameterTest("fn(x, y, z) {};", new String[]{"x", "y", "z"}),
        };

        for (FunctionParameterTest tt : tests) {
            Lexer lexer = new Lexer(tt.input);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            checkParserErrors(parser);
            ExpressionStatement smtt = (ExpressionStatement) program.statements.get(0);
            FunctionLiteral function = (FunctionLiteral) smtt.expression;
            if (function.parameters.size() != tt.expectedParams.length) {
                println("length parameters wrong. want " + tt.expectedParams.length + ", got=" + function.parameters.size());
                return;
            }

            for (int i = 0; i < tt.expectedParams.length; i++) {
                String ident = tt.expectedParams[i];
                testLiteralExpression(function.parameters.get(i), ident);
            }
        }

        println("success");
    }

    private class FunctionParameterTest {
        String input;
        String[] expectedParams;

        public FunctionParameterTest(String input, String[] expectedParams) {
            this.input = input;
            this.expectedParams = expectedParams;
        }
    }

    public void testCallExpressionParsing() {
        String input = "add(1, 2 * 3, 4 + 5);";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        int len = program.statements.size();
        if (len != 1) {
            println("program.statements does not contain 1 statements. got=" + len);
            return;
        }

        Statement statement = program.statements.get(0);
        if (!(statement instanceof ExpressionStatement)) {
            println("program.Statements[0] is not ExpressionStatement. got=" +
                    statement.getClass().getName());
            return;
        }

        Expression expression = ((ExpressionStatement)statement).expression;
        if (!(expression instanceof CallExpression)) {
            println("expression not CallExpression. got=" +
                    (expression == null ? "null" : expression.getClass().getName()));
            return;
        }

        CallExpression exp = (CallExpression) expression;
        if (!testIdentifier(exp.function, "add")) {
            return;
        }

        if (exp.arguments.size() != 3) {
            println("wrong length of arguments. got=" + exp.arguments.size()) ;
            return;
        }

        testLiteralExpression(exp.arguments.get(0), 1);
        testInfixExpression(exp.arguments.get(1), 2, "*", 3);
        testInfixExpression(exp.arguments.get(2), 4, "+", 5);
        println("success");
    }

    public void testStringLiteralExpression() {
        String input = "\"hello world\";";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof StringLiteral)) {
            println("exp not StringLiteral. got=" + stmt.getClass().getName());
            return;
        }

        StringLiteral literal = (StringLiteral) stmt.expression;
        if (!"hello world".equals(literal.value)) {
            println("literal.Value not hello world. got=" + literal.value);
            return;
        }

        println("success");
    }

    public void testParsingArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof ArrayLiteral)) {
            println("exp not ArrayLiteral. got=" + stmt.getClass().getName());
            return;
        }

        ArrayLiteral array = (ArrayLiteral) stmt.expression;
        if (array.elements.size() != 3) {
            println("len(array.Elements) not 3. got=" + array.elements.size());
            return;
        }

        testIntegerLiteral(array.elements.get(0), 1);
        testInfixExpression(array.elements.get(1), 2, "*", 2);
        testInfixExpression(array.elements.get(2), 3, "+", 3);
        println("success");
    }

    public void testParsingIndexExpressions() {
        String input = "myArray[1 + 1]";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof IndexExpression)) {
            println("exp not IndexExpression. got=" + stmt.getClass().getName());
            return;
        }

        IndexExpression indexExp = (IndexExpression) stmt.expression;
        if (!testIdentifier(indexExp.left, "myArray")) {
            return;
        }

        if (!testInfixExpression(indexExp.index, 1, "+", 1)) {
            return;
        }

        println("success");
    }

    public void testParsingHashLiteralsStringKeys() {
        String input = "{\"one\": 1, \"two\": 2, \"three\": 3}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof HashLiteral)) {
            println("exp not HashLiteral. got=" + stmt.getClass().getName());
            return;
        }

        HashLiteral hash = (HashLiteral) stmt.expression;
        if (hash.pairs.size() != 3) {
            println("hash.Pairs has wrong length. got=" + hash.pairs.size());
            return;
        }

        Map<String, Long> expected = new HashMap<String, Long>(){{
            put("one", 1L);
            put("two", 2L);
            put("three", 3L);
        }};

        for (Map.Entry<Expression, Expression> entry : hash.pairs.entrySet()) {
            if (!(entry.getKey() instanceof StringLiteral)) {
                println("key is not ast.StringLiteral. got=" + entry.getKey().getClass().getName());
                continue;
            }

            StringLiteral literal = (StringLiteral) entry.getKey();
            long expectedValue = expected.get(literal.string());
            testIntegerLiteral(entry.getValue(), expectedValue);
        }

        println("success");
    }

    public void testParsingHashLiteralsIntegerKeys() {
        String input = "{1: 1, 2: 2, 3: 3}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof HashLiteral)) {
            println("exp not HashLiteral. got=" + stmt.getClass().getName());
            return;
        }

        HashLiteral hash = (HashLiteral) stmt.expression;
        if (hash.pairs.size() != 3) {
            println("hash.Pairs has wrong length. got=" + hash.pairs.size());
            return;
        }

        Map<Long, Long> expected = new HashMap<Long, Long>(){{
            put(1L, 1L);
            put(2L, 2L);
            put(3L, 3L);
        }};

        for (Map.Entry<Expression, Expression> entry : hash.pairs.entrySet()) {
            if (!(entry.getKey() instanceof IntegerLiteral)) {
                println("key is not ast.IntegerLiteral. got=" + entry.getKey().getClass().getName());
                continue;
            }

            IntegerLiteral literal = (IntegerLiteral) entry.getKey();
            long expectedValue = expected.get(literal.value);
            testIntegerLiteral(entry.getValue(), expectedValue);
        }

        println("success");
    }

    public void testParsingHashLiteralsBooleanKeys() {
        String input = "{true: 1, false: 2}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof HashLiteral)) {
            println("exp not HashLiteral. got=" + stmt.getClass().getName());
            return;
        }

        HashLiteral hash = (HashLiteral) stmt.expression;
        if (hash.pairs.size() != 2) {
            println("hash.Pairs has wrong length. got=" + hash.pairs.size());
            return;
        }

        Map<java.lang.Boolean, Long> expected = new HashMap<java.lang.Boolean, Long>(){{
            put(true, 1L);
            put(false, 2L);
        }};

        for (Map.Entry<Expression, Expression> entry : hash.pairs.entrySet()) {
            if (!(entry.getKey() instanceof Boolean)) {
                println("key is not ast.Boolean. got=" + entry.getKey().getClass().getName());
                continue;
            }

            Boolean bool = (Boolean) entry.getKey();
            long expectedValue = expected.get(bool.value);
            testIntegerLiteral(entry.getValue(), expectedValue);
        }

        println("success");
    }

    public void testParsingEmptyHashLiteral() {
        String input = "{}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof HashLiteral)) {
            println("exp not HashLiteral. got=" + stmt.getClass().getName());
            return;
        }

        HashLiteral hash = (HashLiteral) stmt.expression;
        if (hash.pairs.size() != 0) {
            println("hash.Pairs has wrong length. got=" + hash.pairs.size());
            return;
        }

        println("success");
    }

    public void testParsingHashLiteralsWithExpressions() {
        String input = "{\"one\": 0 + 1, \"two\": 10 - 8, \"three\": 15 / 5}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
        if (!(stmt.expression instanceof HashLiteral)) {
            println("exp not HashLiteral. got=" + stmt.getClass().getName());
            return;
        }

        HashLiteral hash = (HashLiteral) stmt.expression;
        if (hash.pairs.size() != 3) {
            println("hash.Pairs has wrong length. got=" + hash.pairs.size());
            return;
        }

        Map<String, HashTestFunc> tests = new HashMap<String, HashTestFunc>(){{
            put("one", new HashTestFunc(){

                @Override
                public void func(Expression e) {
                    testInfixExpression(e, 0, "+", 1);
                }
            });
            put("two", new HashTestFunc(){

                @Override
                public void func(Expression e) {
                    testInfixExpression(e, 10, "-", 8);
                }
            });
            put("three", new HashTestFunc(){

                @Override
                public void func(Expression e) {
                    testInfixExpression(e, 15, "/", 5);
                }
            });
        }};

        for (Map.Entry<Expression, Expression> entry : hash.pairs.entrySet()) {
            if (!(entry.getKey() instanceof StringLiteral)) {
                println("key is not ast.StringLiteral. got=" + entry.getKey().getClass().getName());
                continue;
            }

            StringLiteral literal = (StringLiteral) entry.getKey();
            HashTestFunc testFunc = tests.get(literal.string());
            if (testFunc == null) {
                println("No test function for key " + literal.string() + " found");
                continue;
            }

            testFunc.func(entry.getValue());
        }

        println("success");
    }

    private interface HashTestFunc {
        void func(Expression e);
    }
}
