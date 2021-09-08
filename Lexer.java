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
        DIGIT, CHAR, QUOTATION, BACKSLASH, 
        EQUALS, DOT, NEWLINE, TAB
    };
    private HashMap<MATCH_TYPE, Pattern> regex_patterns = new HashMap<MATCH_TYPE, Pattern>();
    private HashMap<String, Token> reserved_words = new HashMap<String, Token>();

    public Lexer(String input) {
        this.input = input;
        this.compile_regex();
        this.load_reserved();
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
        String lexemeValue;
        Token token;

        if (this.regex_patterns[MATCH_TYPE.CHAR].match(currentChar) {
            Token.UNKNOWN
            lexemeValue = findCharSequence();
        } else if(this.regex_patterns[MATCH_TYPE.DIGIT].match(currentChar)) {
            lexemeValue = findDigitSequence();
        }

        return new Lexeme(Token.UNKNOWN, 1, 1); // for test 
    }

    private Lexeme findCharSequence() { 
        // should allow characters trailing numbers(i.e. foo123)
        // valid: foo123
        // invalid: 123foo (cannot start with digit)
        const pattern = this.regex_patterns[MATCH_TYPE.CHAR];
        StringBuilder lexemeValue = new StringBuilder();

        while (!pattern.match(this.getCurrentCharacter())) {
            lexemeValue.append(this.getCurrentCharacter());
            this.cursor_forward();
        }

        return new Lexeme(
            Token.IDENTIFIER, 
            lexemeValue.toString(), 
            this.getCursorLine(),
            this.getCursorLine()
        )
    }

    private String findDigitSequence() { 
        // raw digits(i.e. 1234, 0001, 0101)
        // trailing zeros are allowed(but get tokenized without)
        // i.e 0001 becomes 1
        const pattern = this.regex_patterns[MATCH_TYPE.DIGIT];
        StringBuilder lexemeValue = new StringBuilder();

        while (!pattern.match(this.getCurrentCharacter())) {
            lexemeValue.append(this.getCurrentCharacter());
            this.cursor_forward();
        }

        return lexemeValue.toString();
    }

    private void findEquals() {
        const pattern = this.regex_patterns[MATCH_TYPE.EQUALS];
        StringBuilder lexemeValue = new StringBuilder();

        if (pattern.match(this.currentChar())) {
            
        }
    }

    private void compile_regex() {
        this.regex_patterns.put(MATCH_TYPE.DIGIT, Pattern.compile("\\d"));
        this.regex_patterns.put(MATCH_TYPE.CHAR, Pattern.compile("\\D"));
        this.regex_patterns.put(MATCH_TYPE.QUOTATION, Pattern.compile("\"|\'"));
        this.regex_patterns.put(MATCH_TYPE.BACKSLASH, Pattern.compile("\\"));
        this.regex_patterns.put(MATCH_TYPE.EQUALS, Pattern.compile("="));
        this.regex_patterns.put(MATCH_TYPE.DOT, Pattern.compile("\\."));
    }

    private void load_reserved() {
        this.reserved_words.put("if", Token.IF);
        this.reserved_words.put("and", Token.AND);
        this.reserved_words.put("func", Token.FUNCTION);
    }

    public void move_cursor(int offset) {
        this.cursor += offset;
    }

    public void cursor_forward() {
        this.cursor += 1;
    }

    public void cursor_back() {
        this.cursor -= 1;
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