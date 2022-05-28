package ye.chilyn.monkey;

import static ye.chilyn.monkey.Printer.print;
import static ye.chilyn.monkey.Printer.println;

import java.util.List;
import java.util.Scanner;

import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.object.Environment;
import ye.chilyn.monkey.object.Object;
import ye.chilyn.monkey.test.ASTTest;
import ye.chilyn.monkey.test.EvaluatorTest;
import ye.chilyn.monkey.test.LexerTest;
import ye.chilyn.monkey.test.ParserTest;

public class Main {
    public static void main(String[] args) {
        LexerTest lexerTest = new LexerTest();
//        lexerTest.testNextToken();

//        startREPL();

        ParserTest parserTest = new ParserTest();
//        parserTest.testLetStatements();
//        parserTest.testReturnStatements();
//        parserTest.testIdentifierExpression();
//        parserTest.testIntegerLiteralExpression();
//        parserTest.testParsingPrefixExpressions();
//        parserTest.testParsingInfixExpressions();
//        parserTest.testOperatorPrecedenceParsing();
//        parserTest.testBooleanExpression();
//        parserTest.testIfExpression();
//        parserTest.testIfElseExpression();
//        parserTest.testFunctionLiteralParsing();
//        parserTest.testFunctionParameterParsing();
//        parserTest.testCallExpressionParsing();
//        parserTest.testStringLiteralExpression();

//        ASTTest test = new ASTTest();
//        test.testString();

        EvaluatorTest test = new EvaluatorTest();
//        test.testEvalIntegerExpression();
//        test.testEvalBooleanExpression();
//        test.testBangOperator();
//        test.testIfElseExpressions();
//        test.testReturnStatements();
//        test.testErrorHandling();
//        test.testFunctionObject();
//        test.testFunctionApplication();
//        test.testClosures();
//        test.testStringLiteral();
        test.testStringConcatenation();
    }

    public static void startREPL() {
        println("This is the Monkey programming language!");
        println("Feel free to type in commands");
        Scanner scan = new Scanner(System.in);
        Environment env = new Environment();
        String prompt = ">> ";
        while (true) {
            print(prompt);
            String line = scan.nextLine();
            Lexer l = new Lexer(line);
            Parser p = new Parser(l);
            Program program = p.parseProgram();
            if (p.errors().size() != 0) {
                printParserErrors(p.errors());
                continue;
            }

            Object evaluated = new Evaluator().eval(program, env);
            if (evaluated != null) {
                println(evaluated.inspect());
            }
        }
    }

    private static void printParserErrors(List<String> errors) {
        println("Woops! We ran into some monkey business here!");
        println("parser errors:");
        for (String msg : errors) {
            println("\t" + msg);
        }
    }
}
