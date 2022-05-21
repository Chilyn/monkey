package ye.chilyn.monkey.test;

import java.util.List;

import static ye.chilyn.monkey.Printer.print;
import static ye.chilyn.monkey.Printer.println;

import ye.chilyn.monkey.Lexer;
import ye.chilyn.monkey.Parser;
import ye.chilyn.monkey.ast.Expression;
import ye.chilyn.monkey.ast.ExpressionStatement;
import ye.chilyn.monkey.ast.Identifier;
import ye.chilyn.monkey.ast.InfixExpression;
import ye.chilyn.monkey.ast.IntegerLiteral;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.ReturnStatement;
import ye.chilyn.monkey.ast.Statement;

public class ParserTest {

    public void testLetStatements() {
        String input = "let x  5;" +
                "let y = 10;" +
                "let foobar = 838383;";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();
        checkParserErrors(parser);
        if (program == null) {
            println("parseProgram() returned null");
            return;
        }

        int len = program.statements.size();
        if (len != 3) {
            println("program.statements does not contain 3 statements. got=" + len);
            return;
        }

        String[] expectedIndentifiers = {"x", "y", "foobar"};
        for (int i = 0; i < expectedIndentifiers.length; i++) {
            Statement statement = program.statements.get(i);
            if (!testLetStatement(statement, expectedIndentifiers[i])) {
                return;
            }
        }
        println("success");
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

        System.exit(0);
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
            if (!(expression instanceof PrefixExpression)) {
                println("expression is not PrefixExpression. got=" +
                        (expression == null ? "null" : expression.getClass().getName()));
                continue;
            }

            PrefixExpression prefixExpression = (PrefixExpression) expression;
            if (!t.operator.equals(prefixExpression.operator)) {
                println("prefixExpression.operator is not '" + t.operator +
                        "'. got=" + (prefixExpression == null ? "null" : expression.getClass().getName()));
                continue;
            }

            if (!testIntegerLiteral(prefixExpression.right, t.integerValue)) {
                return;
            }

        }
        println("success");
    }

    private boolean testIntegerLiteral(Expression right , long value) {
        if (!(right instanceof IntegerLiteral)) {
            println("right not IntegerLiteral. got=" + right.getClass().getName());
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

    private class PrefixTest {
        String input;
        String operator;
        long integerValue;

        public PrefixTest(String input, String operator, long integerValue) {
            this.input = input;
            this.operator = operator;
            this.integerValue = integerValue;
        }
    }

    public void testParsingInfixExpressions() {
        InfixTest[] infixTests = {
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
            if (!(expression instanceof InfixExpression)) {
                println("expression is not InfixExpression. got=" +
                        (expression == null ? "null" : expression.getClass().getName()));
                continue;
            }

            InfixExpression infixExpression = (InfixExpression) expression;
            if (!testIntegerLiteral(infixExpression.left, t.leftValue)) {
                return;
            }

            if (!t.operator.equals(infixExpression.operator)) {
                println("infixExpression.operator is not '" + t.operator +
                        "'. got=" + (infixExpression == null ? "null" : expression.getClass().getName()));
                continue;
            }

            if (!testIntegerLiteral(infixExpression.right, t.rightValue)) {
                return;
            }

        }
        println("success");
    }

    private class InfixTest {
        String input;
        long leftValue;
        String operator;
        long rightValue;

        public InfixTest(String input, long leftValue, String operator, long rightValue) {
            this.input = input;
            this.leftValue = leftValue;
            this.operator = operator;
            this.rightValue = rightValue;
        }
    }

    public void testOperatorPrecedenceParsing() {
        OperatorPrecedenceTest[] tests = {
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
}
