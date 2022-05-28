package ye.chilyn.monkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ye.chilyn.monkey.ast.BlockStatement;
import ye.chilyn.monkey.ast.Boolean;
import ye.chilyn.monkey.ast.CallExpression;
import ye.chilyn.monkey.ast.Expression;
import ye.chilyn.monkey.ast.ExpressionStatement;
import ye.chilyn.monkey.ast.FunctionLiteral;
import ye.chilyn.monkey.ast.Identifier;
import ye.chilyn.monkey.ast.IfExpression;
import ye.chilyn.monkey.ast.InfixExpression;
import ye.chilyn.monkey.ast.IntegerLiteral;
import ye.chilyn.monkey.ast.LetStatement;
import ye.chilyn.monkey.ast.PrefixExpression;
import ye.chilyn.monkey.ast.Program;
import ye.chilyn.monkey.ast.ReturnStatement;
import ye.chilyn.monkey.ast.Statement;
import ye.chilyn.monkey.ast.StringLiteral;

public class Parser {
    private static final int LOWEST = 0;
    private static final int EQUALS = 1;
    private static final int LESSGREATER = 2;
    private static final int SUM = 3;
    private static final int PRODUCT = 4;
    private static final int PREFIX = 5;
    private static final int CALL = 6;
    private static final int INDEX = 7;
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;
    private List<String> errors;
    private Map<String, PrefixParseFn> prefixParseFns;
    private Map<String, InfixParseFn> infixParseFns;
    private Map<String, Integer> precedences = new HashMap<String, Integer>() {{
        put(TokenType.EQ, EQUALS);
        put(TokenType.NOT_EQ, EQUALS);
        put(TokenType.LT, LESSGREATER);
        put(TokenType.GT, LESSGREATER);
        put(TokenType.PLUS, SUM);
        put(TokenType.MINUS, SUM);
        put(TokenType.SLASH, PRODUCT);
        put(TokenType.ASTERISK, PRODUCT);
        put(TokenType.LPAREN, CALL);
    }};

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        prefixParseFns = new HashMap<>();
        registerPrefix(TokenType.IDENT, parseIdentifier);
        registerPrefix(TokenType.INT, parseIntegerLiteral);
        registerPrefix(TokenType.BANG, parsePrefixExpression);
        registerPrefix(TokenType.MINUS, parsePrefixExpression);
        registerPrefix(TokenType.TRUE, parseBoolean);
        registerPrefix(TokenType.FALSE, parseBoolean);
        registerPrefix(TokenType.LPAREN, parseGroupedExpression);
        registerPrefix(TokenType.IF, parseIfExpression);
        registerPrefix(TokenType.FUNCTION, parseFunctionLiteral);
        registerPrefix(TokenType.STRING, parseStringLiteral);
        infixParseFns = new HashMap<>();
        registerInfix(TokenType.PLUS, parseInfixExpression);
        registerInfix(TokenType.MINUS, parseInfixExpression);
        registerInfix(TokenType.SLASH, parseInfixExpression);
        registerInfix(TokenType.ASTERISK, parseInfixExpression);
        registerInfix(TokenType.EQ, parseInfixExpression);
        registerInfix(TokenType.NOT_EQ, parseInfixExpression);
        registerInfix(TokenType.LT, parseInfixExpression);
        registerInfix(TokenType.GT, parseInfixExpression);
        registerInfix(TokenType.LPAREN,  parseCallExpression);
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
            case TokenType.RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStatement();
        }
    }

    private LetStatement parseLetStatement() {
        LetStatement statement = new LetStatement(curToken);
        if (!expectPeek(TokenType.IDENT)) {
            return null;
        }

        statement.name = new Identifier(curToken, curToken.getLiteral());
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }

        nextToken();
        statement.value = parseExpression(LOWEST);
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return statement;
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement statement = new ReturnStatement(curToken);
        nextToken();

        statement.returnValue = parseExpression(LOWEST);
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return statement;
    }

    private ExpressionStatement parseExpressionStatement() {
        ExpressionStatement statement = new ExpressionStatement(curToken);
        statement.expression = parseExpression(LOWEST);
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return statement;
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement(curToken);
        block.statements = new ArrayList<>();
        nextToken();
        while (!curTokenIs(TokenType.RBRACE)) {
            Statement smtt = parseStatement();
            if (smtt != null) {
                block.statements.add(smtt);
            }
            nextToken();
        }
        return block;
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

    private Expression parseExpression(int precedence) {
        PrefixParseFn prefix = prefixParseFns.get(curToken.getType());
        if (prefix == null) {
            noPrefixParseFnError(curToken.getType());
            return null;
        }

        Expression leftExp = prefix.prefixParseFn();
        while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            InfixParseFn infix = infixParseFns.get(peekToken.getType());
            if (infix == null) {
                return leftExp;
            }

            nextToken();
            leftExp = infix.infixParseFn(leftExp);
        }
        return leftExp;
    }

    private void noPrefixParseFnError(String tokenType) {
        String msg = "no prefix parse function for " + tokenType + " found";
        errors.add(msg);
    }

    private int peekPrecedence() {
        if (precedences.containsKey(peekToken.getType())) {
            return precedences.get(peekToken.getType());
        }

        return LOWEST;
    }

    private int curPrecedence() {
        if (precedences.containsKey(curToken.getType())) {
            return precedences.get(curToken.getType());
        }

        return LOWEST;
    }

    public List<String> errors() {
        return errors;
    }

    private void peekError(String tokenType) {
        String msg = "expected next token to be " + tokenType + ", got " + peekToken.getType() + " instead";
        errors.add(msg);
    }

    private void registerPrefix(String tokenType, PrefixParseFn prefixParseFn) {
        prefixParseFns.put(tokenType, prefixParseFn);
    }

    private void registerInfix(String tokenType, InfixParseFn infixParseFn) {
        infixParseFns.put(tokenType, infixParseFn);
    }

    interface PrefixParseFn {
        Expression prefixParseFn();
    }

    interface InfixParseFn {
        Expression infixParseFn(Expression left);
    }

    private PrefixParseFn parseIdentifier = new PrefixParseFn() {

        @Override
        public Expression prefixParseFn() {
            return new Identifier(curToken, curToken.getLiteral());
        }
    };

    private PrefixParseFn parseIntegerLiteral = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            IntegerLiteral literal = new IntegerLiteral(curToken);
            try {
                literal.value = Long.parseLong(curToken.getLiteral());
            } catch (NumberFormatException e) {
                String msg = "could not parse " + curToken.getLiteral() + " as integer";
                errors.add(msg);
                return null;
            }
            return literal;
        }
    };

    private PrefixParseFn parsePrefixExpression = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            PrefixExpression expression = new PrefixExpression(
                    curToken,
                    curToken.getLiteral()
            );
            nextToken();
            expression.right = parseExpression(PREFIX);
            return expression;
        }
    };

    private PrefixParseFn parseBoolean = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            return new Boolean(curToken, curTokenIs(TokenType.TRUE));
        }
    };

    private PrefixParseFn parseGroupedExpression = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            nextToken();
            Expression exp = parseExpression(LOWEST);
            if (!expectPeek(TokenType.RPAREN)) {
                return null;
            }

            return exp;
        }
    };

    private PrefixParseFn parseIfExpression = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            IfExpression expression = new IfExpression(curToken);
            if (!expectPeek(TokenType.LPAREN)) {
                return null;
            }

            nextToken();
            expression.condition = parseExpression(LOWEST);
            if (!expectPeek(TokenType.RPAREN)) {
                return null;
            }

            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }

            expression.consequence = parseBlockStatement();
            if (peekTokenIs(TokenType.ELSE)) {
                nextToken();
                if (!expectPeek(TokenType.LBRACE)) {
                    return null;
                }

                expression.alternative = parseBlockStatement();
            }
            return expression;
        }
    };

    private PrefixParseFn parseFunctionLiteral = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            FunctionLiteral expression = new FunctionLiteral(curToken);
            if (!expectPeek(TokenType.LPAREN)) {
                return null;
            }

            expression.parameters = parseFunctionParameters();
            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }

            expression.body = parseBlockStatement();
            return expression;
        }

        private List<Identifier> parseFunctionParameters() {
            List<Identifier> identifiers = new ArrayList<>();
            if (peekTokenIs(TokenType.RPAREN)) {
                nextToken();
                return identifiers;
            }

            nextToken();
            identifiers.add(new Identifier(curToken, curToken.getLiteral()));
            while (peekTokenIs(TokenType.COMMA)) {
                nextToken();
                nextToken();
                identifiers.add(new Identifier(curToken, curToken.getLiteral()));
            }

            if (!expectPeek(TokenType.RPAREN)) {
                return null;
            }

            return identifiers;
        }
    };

    private PrefixParseFn parseStringLiteral = new PrefixParseFn() {
        @Override
        public Expression prefixParseFn() {
            return new StringLiteral(curToken, curToken.getLiteral());
        }
    };

    private InfixParseFn parseInfixExpression = new InfixParseFn() {
        @Override
        public Expression infixParseFn(Expression left) {
            InfixExpression expression = new InfixExpression(
                    curToken,
                    left,
                    curToken.getLiteral()
            );
            int precedence = curPrecedence();
            nextToken();
            expression.right = parseExpression(precedence);
            return expression;
        }
    };

    private InfixParseFn parseCallExpression = new InfixParseFn() {
        @Override
        public Expression infixParseFn(Expression function) {
            CallExpression expression = new CallExpression(curToken, function);
            expression.arguments = parseCallArguments();
            return expression;
        }

        private List<Expression> parseCallArguments() {
            List<Expression> args = new ArrayList<>();
            if (peekTokenIs(TokenType.RPAREN)) {
                nextToken();
                return args;
            }

            nextToken();
            args.add(parseExpression(LOWEST));
            while (peekTokenIs(TokenType.COMMA)) {
                nextToken();
                nextToken();
                args.add(parseExpression(LOWEST));
            }

            if (!expectPeek(TokenType.RPAREN)) {
                return null;
            }
            return args;
        }
    };
}
