package compiler;

import compiler.LexemeToken;

public class Lexeme {
    private LexemeToken token;
    private int line;
    private int column;
    private Object value;

    public Lexeme(LexemeToken token, Object value, int line, int column) {
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

    public LexemeToken getToken() {
        return this.token;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return String.format(
          "{%n\tToken: %s,%n\tValue: %s,%n\tLine, Column: %d, %d%n}%n",
          this.getToken().toString(), this.getValue(), this.getLine(), this.getColumn()
        );
    }
}
