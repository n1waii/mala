/*
public enum LexerState {
    FREE,
    DEFINING,
    ASSIGNING,
    BRANCHING,

}
*/

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String input;
    private int cursor;
    private HashMap<Integer, Lexeme> lexeme_cache = new HashMap<Integer, Lexeme>();
    private int line_number;
    private int column_number;
    private static enum MATCH_TYPE {
        DIGIT, CHAR, QUOTATION, BACKSLASH, EQUALS, DOT, NEWLINE, TAB
    };
    private HashMap<MATCH_TYPE, Pattern> regex_patterns = new HashMap<MATCH_TYPE, Pattern>();

    public Lexer(String input) {
        this.input = input;
        this.compile_regex();
    }

    public Lexeme getLexeme() {
        Lexeme topCache = this.lexeme_cache.get(this.cursor);
        Lexeme lexeme = (topCache != null) ? topCache : this.findNewLexeme();

        return lexeme;
    }

    private String getCurrentCharacter() {
        if (this.isExhausted()) {
            return null;
        }

        return Character.toString(this.input.charAt(this.cursor));
    }

    private Lexeme findNewLexeme() {
        if (this.isExhausted()) {
            return null;
        }

        String currentChar = this.getCurrentCharacter();

        return new Lexeme(Token.UNKNOWN, 1, 1); // for test 
    }

    private void compile_regex() {
        this.regex_patterns.put(MATCH_TYPE.DIGIT, Pattern.compile("\\d"));
        this.regex_patterns.put(MATCH_TYPE.CHAR, Pattern.compile("\\D"));
        this.regex_patterns.put(MATCH_TYPE.QUOTATION, Pattern.compile("\"|\'"));
        this.regex_patterns.put(MATCH_TYPE.BACKSLASH, Pattern.compile("\\"));
        this.regex_patterns.put(MATCH_TYPE.EQUALS, Pattern.compile("="));
        this.regex_patterns.put(MATCH_TYPE.DOT, Pattern.compile("\\."));
    }

    public void move_cursor(int offset) {
        this.cursor += offset;
    }

    public boolean isExhausted() {
        return this.cursor > input.length();
    }

    public int getCursorLine() {
        return this.line_number;
    }

    public int getCursorColumn() {
        return this.column_number;
    }
}