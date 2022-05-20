package ye.chilyn.monkey;

import java.util.ArrayList;
import java.util.List;

import ye.chilyn.monkey.ast.Identifier;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.Statement;

public class Parser {
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;
    private List<String> errors;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        nextToken();
        nextToken();
    }

    public void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Program parseProgram() {
        Program program = new Program();
        while (!TokenType.EOF.equals(curToken.getType())) {
            Statement statement = parseStatement();
            if (statement != null) {
                program.statements.add(statement);
            }
            nextToken();
        }
        return program;
    }

    private Statement parseStatement() {
        switch (curToken.getType()) {
            case TokenType.LET:
                return parseLetStatement();
            default:
                return null;
        }
    }

    private Statement parseLetStatement() {
        LetStatement statement = new LetStatement(curToken);
        if (!expectPeek(TokenType.IDENT)) {
            return null;
        }

        statement.name = new Identifier(curToken, curToken.getLiteral());
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }

        while (!curTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return statement;
    }

    private boolean curTokenIs(String tokenType) {
        return tokenType.equals(curToken.getType());
    }

    private boolean peekTokenIs(String tokenType) {
        return tokenType.equals(peekToken.getType());
    }

    private boolean expectPeek(String tokenType) {
        if (peekTokenIs(tokenType)) {
            nextToken();
            return true;
        } else {
            peekError(tokenType);
            return false;
        }
    }

    public List<String> errors() {
        return errors;
    }

    private void peekError(String tokenType) {
        String msg = "expected next token to be " + tokenType + ", got " + peekToken.getType() + " instead";
        errors.add(msg);
    }
}
