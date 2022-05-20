package ye.chilyn.monkey.test;

import java.util.List;

import static ye.chilyn.monkey.Printer.print;
import static ye.chilyn.monkey.Printer.println;

import ye.chilyn.monkey.Lexer;
import ye.chilyn.monkey.Parser;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.Program;
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
}
