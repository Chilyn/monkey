package ye.chilyn.monkey;

import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private String input;
    private int position;
    private int readPosition;
    private String ch = "";
    private Map<String, String> keywordsMap = new HashMap<>();

    public Lexer(String input) {
        this.input = input;
        readChar();
        keywordsMap.put("fn", TokenType.FUNCTION);
        keywordsMap.put("let", TokenType.LET);
        keywordsMap.put("true", TokenType.TRUE);
        keywordsMap.put("false", TokenType.FALSE);
        keywordsMap.put("if", TokenType.IF);
        keywordsMap.put("else", TokenType.ELSE);
        keywordsMap.put("return", TokenType.RETURN);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getReadPosition() {
        return readPosition;
    }

    public void setReadPosition(int readPosition) {
        this.readPosition = readPosition;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public Token nextToken() {
        Token tok = null;
        skipWhiteSpace();
        switch (ch) {
            case "=":
                if ("=".equals(peekChar())) {
                    String ch = this.ch;
                    readChar();
                    tok = new Token(TokenType.EQ, ch + this.ch);
                } else {
                    tok = new Token(TokenType.ASSIGN, ch);
                }
                break;
            case ";":
                tok = new Token(TokenType.SEMICOLON, ch);
                break;
            case "(":
                tok = new Token(TokenType.LPAREN, ch);
                break;
            case ")":
                tok = new Token(TokenType.RPAREN, ch);
                break;
            case ",":
                tok = new Token(TokenType.COMMA, ch);
                break;
            case "+":
                tok = new Token(TokenType.PLUS, ch);
                break;
            case "-":
                tok = new Token(TokenType.MINUS, ch);
                break;
            case "!":
                if ("=".equals(peekChar())) {
                    String ch = this.ch;
                    readChar();
                    tok = new Token(TokenType.NOT_EQ, ch + this.ch);
                } else {
                    tok = new Token(TokenType.BANG, ch);
                }
                break;
            case "*":
                tok = new Token(TokenType.ASTERISK, ch);
                break;
            case "/":
                tok = new Token(TokenType.SLASH, ch);
                break;
            case "<":
                tok = new Token(TokenType.LT, ch);
                break;
            case ">":
                tok = new Token(TokenType.GT, ch);
                break;
            case "{":
                tok = new Token(TokenType.LBRACE, ch);
                break;
            case "}":
                tok = new Token(TokenType.RBRACE, ch);
                break;
            case "":
                tok = new Token(TokenType.EOF, ch);
                break;
            default:
                if (isLetter(ch)) {
                    String literal = readIdentifier();
                    String type = lookupIdent(literal);
                    tok = new Token(type, literal);
                } else if (isDigit(ch)) {
                    String literal = readNumber();
                    String type = TokenType.INT;
                    tok = new Token(type, literal);
                } else {
                    tok = new Token(TokenType.ILLEGAL, ch);
                }
                return tok;
        }

        readChar();
        return tok;
    }

    private void readChar() {
        if (readPosition >= input.length()) {
            ch = "";
        } else {
            ch = input.substring(readPosition, readPosition + 1);
        }
        position = readPosition;
        readPosition += 1;
    }

    private String readIdentifier() {
        int position = this.position;
        while (isLetter(ch)) {
            readChar();
        }
        return input.substring(position, this.position);
    }

    private String lookupIdent(String identifier) {
        if (keywordsMap.containsKey(identifier)) {
            return keywordsMap.get(identifier);
        } else {
            return TokenType.IDENT;
        }
    }

    private void skipWhiteSpace() {
        while (" ".equals(ch) || "\t".equals(ch) || "\n".equals(ch) || "\r".equals(ch)) {
            readChar();
        }
    }

    private String readNumber() {
        int position = this.position;
        while (isDigit(ch)) {
            readChar();
        }
        return input.substring(position, this.position);
    }

    private String peekChar() {
        if (readPosition >= input.length()) {
            return "";
        } else {
            return input.substring(readPosition, readPosition + 1);
        }
    }

    private boolean isLetter(String ch) {
        if (ch == null || ch.length() == 0) {
            return false;
        }

        return ('a' <= ch.charAt(0) && ch.charAt(0) <= 'z') ||
                ('A' <= ch.charAt(0) && ch.charAt(0) <= 'Z') ||
                "_".equals(ch);
    }

    private boolean isDigit(String ch) {
        if (ch == null || ch.length() == 0) {
            return false;
        }

        return '0' <= ch.charAt(0) && ch.charAt(0) <= '9';
    }
}
