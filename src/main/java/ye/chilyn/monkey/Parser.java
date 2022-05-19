package ye.chilyn.monkey;

import ye.chilyn.monkey.ast.Program;

public class Parser {
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        nextToken();
        nextToken();
    }

    public void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Program parseProgram() {
        return null;
    }
}
