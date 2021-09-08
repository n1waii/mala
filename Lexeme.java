enum Token {
    INTEGER, STRING, IDENTIFIER, LEFT_PARENTHESIS, 
    RIGHT_PARENTHESIS, SEMICOLON, ASSIGNMENT, UNKNOWN,
    IF, AND, OR, FUNCTION
}

public class Lexeme {
    private Token token;
    private int line;
    private int column;

    public Lexeme(Token token, value, int line, int column) {
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
    
    public Token getValue() {
        return this.value;
    }
}

/*
 * local andOne = 1;
 * 
 * if (foo == 1) {
 * 
 * }
 * 
 * println(foo)
 */
