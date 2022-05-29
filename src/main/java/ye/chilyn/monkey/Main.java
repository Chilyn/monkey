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
import ye.chilyn.monkey.test.ObjectTest;
import ye.chilyn.monkey.test.ParserTest;

public class Main {
    public static void main(String[] args) {
//        LexerTest lexerTest = new LexerTest();
//        lexerTest.testNextToken();

//        ParserTest parserTest = new ParserTest();
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
//        parserTest.testParsingArrayLiterals();
//        parserTest.testParsingIndexExpressions();
//        parserTest.testParsingEmptyHashLiteral();
//        parserTest.testParsingHashLiteralsStringKeys();
//        parserTest.testParsingHashLiteralsBooleanKeys();
//        parserTest.testParsingHashLiteralsIntegerKeys();
//        parserTest.testParsingHashLiteralsWithExpressions();

//        ASTTest test = new ASTTest();
//        test.testString();

//        EvaluatorTest evaluatorTest = new EvaluatorTest();
//        evaluatorTest.testEvalIntegerExpression();
//        evaluatorTest.testEvalBooleanExpression();
//        evaluatorTest.testBangOperator();
//        evaluatorTest.testIfElseExpressions();
//        evaluatorTest.testReturnStatements();
//        evaluatorTest.testErrorHandling();
//        evaluatorTest.testFunctionObject();
//        evaluatorTest.testFunctionApplication();
//        evaluatorTest.testClosures();
//        evaluatorTest.testStringLiteral();
//        evaluatorTest.testStringConcatenation();
//        evaluatorTest.testBuiltinFunctions();
//        evaluatorTest.testArrayLiterals();
//        evaluatorTest.testArrayIndexExpressions();
//        evaluatorTest.testHashLiterals();
//        evaluatorTest.testHashIndexExpressions();

//        ObjectTest objectTest = new ObjectTest();
//        objectTest.testStringHashKey();

        startREPL();
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
