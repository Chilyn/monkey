package ye.chilyn.monkey;

import static ye.chilyn.monkey.Printer.print;
import static ye.chilyn.monkey.Printer.println;

import java.util.Scanner;

import ye.chilyn.monkey.test.ASTTest;
import ye.chilyn.monkey.test.LexerTest;
import ye.chilyn.monkey.test.ParserTest;

public class Main {
    public static void main(String[] args) {
//        LexerTest lexerTest = new LexerTest();
//        lexerTest.testNextToken();

//        startREPL();

        ParserTest parserTest = new ParserTest();
//        parserTest.testReturnStatements();
//        parserTest.testIdentifierExpression();
//        parserTest.testIntegerLiteralExpression();
//        parserTest.testParsingPrefixExpressions();
//        parserTest.testParsingInfixExpressions();
        parserTest.testOperatorPrecedenceParsing();

//        ASTTest test = new ASTTest();
//        test.testString();
    }

    public static void startREPL() {
        println("This is the Monkey programming language!");
        println("Feel free to type in commands");
        Scanner scan = new Scanner(System.in);
        String prompt = ">> ";
        while (true) {
            print(prompt);
            String line = scan.nextLine();
            Lexer l = new Lexer(line);
            for (Token tok = l.nextToken(); !TokenType.EOF.equals(tok.getType()); tok = l.nextToken()) {
                println(tok);
            }
        }
    }
}
