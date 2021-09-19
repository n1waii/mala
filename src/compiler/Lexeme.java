package compiler;

enum Token {
    NUMBER, STRING, IDENTIFIER, LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS, SEMICOLON, ASSIGNMENT, UNKNOWN,
    COMP_EQUAL, COMP_GREATER, COMP_LESS, COMP_LESS_EQUAL, COMP_GREATER_EQUAL,
    IF, AND, OR, FUNCTION, LET, RETURN
}

public class Lexeme {
    private Token token;
    private int line;
    private int column;
    private String value;

    public Lexeme(Token token, String value, int line, int column) {
        this.token = token;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public Token getToken() {
        return this.token;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return String.format(
          "{%nToken: %s,%nValue: %s,%nLine, Column: %d %d%n}%n",
          this.getToken().toString(), this.getValue(), this.getLine(), this.getColumn()
        );
    }
}
