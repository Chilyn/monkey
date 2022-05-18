package ye.chilyn.monkey;

public class Token {
    private String type;
    private String literal;

    public Token(String type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", literal='" + literal + '\'' +
                '}';
    }
}
