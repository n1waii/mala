package compiler;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Lexer {
    private String input;
    private int cursor;
    private HashMap<Integer, Lexeme> lexeme_cache = new HashMap<Integer, Lexeme>();
    private int line_number = 1;
    private int column_number = 1;
    private static enum MATCH_TYPE {
        DIGIT, CHAR, QUOTATION, BACKSLASH,
        EQUAL, DOT, NEWLINE, TAB, WHITESPACE
    };
    private static HashMap<MATCH_TYPE, Pattern> regex_patterns = new HashMap<MATCH_TYPE, Pattern>();
    private static HashMap<String, Token> reserved_words = new HashMap<String, Token>();

    public Lexer(String input) {
        this.input = input;
        this.cursor = 0;
        compile_regex();
        load_reserved();
    }

    public Lexeme getLexeme() {
        Lexeme topCache = this.lexeme_cache.get(this.cursor);
        Lexeme lexeme = (topCache != null) ? topCache : this.findNewLexeme();
        // note: add caching here

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

        if (findMatch(MATCH_TYPE.WHITESPACE, currentChar)) {
            return null;
        }

        if (findMatch(MATCH_TYPE.CHAR, currentChar)) {
            return this.findCharSequence();
        } else if (findMatch(MATCH_TYPE.QUOTATION, currentChar)) {
            return this.findStringSequence();
        } else if (findMatch(MATCH_TYPE.DIGIT, currentChar)) {
            return this.findNumberSequence();
        } else if (findMatch(MATCH_TYPE.EQUAL, currentChar)) {
            return this.findEqualSequence();
        }

        return new Lexeme(Token.UNKNOWN, "", this.getCursorLine(), this.getCursorColumn());
    }

    private Lexeme findCharSequence() {
        // should allow characters trailing numbers(i.e. foo123)
        // valid: foo123
        // invalid: 123foo (cannot start with digit)
        StringBuilder lexemeValue = new StringBuilder();
        String currentChar = this.getCurrentCharacter();
        int lineStart = this.getCursorLine();
        int columnStart = this.getCursorColumn();
        Token token;

        while (!this.isExhausted() && (findMatch(MATCH_TYPE.CHAR, currentChar) || findMatch(MATCH_TYPE.DIGIT, currentChar))) {
            lexemeValue.append(currentChar);
            this.cursorForward();
            currentChar = this.getCurrentCharacter();
        }

        token = reserved_words.get(lexemeValue.toString());

        return new Lexeme(
            (token != null) ? token : Token.IDENTIFIER,
            lexemeValue.toString(),
            lineStart,
            columnStart
        );
    }

    private Lexeme findNumberSequence() {
        // raw digits(i.e. 1234, 0001, 0101)
        // trailing zeros are allowed(but get tokenized without)
        // i.e 0001 becomes 1

        StringBuilder lexemeValue = new StringBuilder();
        int lineStart = this.getCursorLine();
        int columnStart = this.getCursorColumn();

        while (!this.isExhausted() &&
            (findMatch(MATCH_TYPE.DIGIT, this.getCurrentCharacter()) || findMatch(MATCH_TYPE.DOT, this.getCurrentCharacter()))) {
            lexemeValue.append(this.getCurrentCharacter());
            this.cursorForward();
        }

        return new Lexeme(
            Token.NUMBER,
            lexemeValue.toString(),
            lineStart,
            columnStart
        );
    }

    private Lexeme findEqualSequence() {
        int lineStart = this.getCursorLine();
        int columnStart = this.getCursorColumn();
        String lexemeValue = "";
        Token token;

        this.cursorForward();
        if (this.isExhausted()) {
          return null;
        }

        if (findMatch(MATCH_TYPE.EQUAL, this.getCurrentCharacter())) {
            token = Token.COMP_EQUAL;
            lexemeValue = "==";
        } else {
            token = Token.ASSIGNMENT;
            lexemeValue = "=";
        }

        return new Lexeme(
            token,
            lexemeValue,
            lineStart,
            columnStart
        );
    }

    private Lexeme findStringSequence() {
      StringBuilder lexemeValue = new StringBuilder();
      int lineStart = this.getCursorLine();
      int columnStart = this.getCursorColumn();

      this.cursorForward();
      if (this.isExhausted()) {
        return null;
      }

      while (!this.isExhausted() && !findMatch(MATCH_TYPE.QUOTATION, this.getCurrentCharacter())) {
        lexemeValue.append(this.getCurrentCharacter());
        this.cursorForward();
      }

      return new Lexeme(
          Token.STRING,
          lexemeValue.toString(),
          lineStart,
          columnStart
      );
    }

    public void moveCursor(int offset) {
        this.cursor += offset;
    }

    public void cursorForward() {
        this.cursor += 1;
        this.column_number += 1;

        if (this.isEOL()) {
            this.column_number = 0;
            this.line_number += 1;
        }
    }

    public boolean isEOL() {
        if (this.isExhausted()) {
          return false;
        }

        String character = this.getCurrentCharacter();
        if (character.contains("\n")) {
          return true;
        } else if (character.contains("\r")) {
          moveCursor(1);
          if (this.isExhausted() || (!this.getCurrentCharacter().contains("\n"))) {
            moveCursor(1);
          }
          return true;
        }

        return false;
    }

    public void cursorBack() {
        this.cursor -= 1;
    }

    public boolean isExhausted() {
        return this.cursor+1 > input.length();
    }

    public int getCursorLine() {
        return this.line_number;
    }

    public int getCursorColumn() {
        return this.column_number;
    }

    private static void compile_regex() {
        regex_patterns.put(MATCH_TYPE.DIGIT, Pattern.compile("\\d"));
        regex_patterns.put(MATCH_TYPE.CHAR, Pattern.compile("[A-Za-z]"));
        regex_patterns.put(MATCH_TYPE.QUOTATION, Pattern.compile("\"|\'"));
        regex_patterns.put(MATCH_TYPE.BACKSLASH, Pattern.compile("\\\\"));
        regex_patterns.put(MATCH_TYPE.EQUAL, Pattern.compile("="));
        regex_patterns.put(MATCH_TYPE.DOT, Pattern.compile("\\."));
        regex_patterns.put(MATCH_TYPE.WHITESPACE, Pattern.compile("\\s"));
    }

    private static void load_reserved() {
        reserved_words.put("let", Token.LET);
        reserved_words.put("if", Token.IF);
        reserved_words.put("and", Token.AND);
        reserved_words.put("func", Token.FUNCTION);
    }

    private static boolean findMatch(MATCH_TYPE matchType, String toFind) {
        return regex_patterns.get(matchType).matcher(toFind).find();
    }
}
