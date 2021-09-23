package compiler;

import java.util.HashMap;
import java.util.regex.Pattern;

import compiler.LexemeToken;

public class Lexer {
    private String input;
    private int cursor;
    //private HashMap<Integer, Lexeme> lexeme_cache = new HashMap<Integer, Lexeme>();
    private int line_number = 1;
    private int column_number = 0;
    private static enum MATCH_TYPE {
        DIGIT, CHAR, QUOTATION, BACKSLASH,
        EQUAL, DOT, NEWLINE, TAB, WHITESPACE
    };
    private static HashMap<MATCH_TYPE, Pattern> regex_patterns = new HashMap<MATCH_TYPE, Pattern>();
    private static HashMap<String, LexemeToken> reserved_words = new HashMap<String, LexemeToken>();

    public Lexer(String input) {
        this.input = input;
        this.cursor = -1;
        compile_regex();
        load_reserved();
    }

    public Lexeme getNextLexeme() {
        this.cursorForward();
        System.out.println(this.cursor);
        Lexeme lexeme = this.findNewLexeme();
        return lexeme;
    }

    private String getCurrentCharacter() {
        if (this.isExhausted()) {
            return null;
        }
        return Character.toString(this.input.charAt(this.cursor));
    }

    private String nextChar() {
      this.cursorForward();
      return this.getCurrentCharacter();
    }

    private Lexeme findNewLexeme() {
        if (this.isExhausted()) {
            return new Lexeme(LexemeToken.EOF, "End Of File", this.getCursorLine(), this.getCursorColumn());
        }

        if (findMatch(MATCH_TYPE.WHITESPACE, this.getCurrentCharacter())) {
            System.out.println(this.getCurrentCharacter() + this.cursor);
            while (!this.isExhausted() && findMatch(MATCH_TYPE.WHITESPACE, this.getCurrentCharacter())) {
              this.cursorForward();
            };

            if (this.isExhausted()) {
              return new Lexeme(LexemeToken.EOF, "End Of File", this.getCursorLine(), this.getCursorColumn());
            }
        }

        String currentChar = this.getCurrentCharacter();
        int currentLine = this.getCursorLine();
        int currentColumn = this.getCursorColumn();
        System.out.println(currentChar + this.cursor);
        switch (currentChar) {
          case "+":
            return new Lexeme(LexemeToken.BINARY_ADD, "+", currentLine, currentColumn);
          case "-":
            return new Lexeme(LexemeToken.BINARY_SUB, "-", currentLine, currentColumn);
          case "*":
            return new Lexeme(LexemeToken.BINARY_MUL, "*", currentLine, currentColumn);
          case "/":
            return new Lexeme(LexemeToken.BINARY_DIV, "/", currentLine, currentColumn);
          case "(":
            return new Lexeme(LexemeToken.LEFT_PAREN, "(", currentLine, currentColumn);
          case ")":
            return new Lexeme(LexemeToken.RIGHT_PAREN, ")", currentLine, currentColumn);
          case ".":
            if (findMatch(MATCH_TYPE.DIGIT, this.nextChar())) {
              this.cursorBack();
              return this.findNumberSequence();
            }
            break;
          case "=":
            if (this.nextChar() == "=") {
                return new Lexeme(LexemeToken.COMP_EQUAL, "==", this.getCursorLine(), this.getCursorColumn());
            } else {
                return new Lexeme(LexemeToken.ASSIGNMENT, "=", this.getCursorLine(), this.getCursorColumn()-1);
            }
          default:
            if (findMatch(MATCH_TYPE.CHAR, currentChar)) {
                return this.findCharSequence();
            } else if (findMatch(MATCH_TYPE.QUOTATION, currentChar)) {
                return this.findStringSequence();
            } else if (findMatch(MATCH_TYPE.DIGIT, currentChar)) {
                return this.findNumberSequence();
            }
        }

        return new Lexeme(LexemeToken.UNKNOWN, "", this.getCursorLine(), this.getCursorColumn());
    }

    private Lexeme findCharSequence() {
        // should allow characters trailing numbers(i.e. foo123)
        // valid: foo123
        // invalid: 123foo (cannot start with digit)
        StringBuilder lexemeValue = new StringBuilder();
        String currentChar = this.getCurrentCharacter();
        int lineStart = this.getCursorLine();
        int columnStart = this.getCursorColumn();
        LexemeToken token;

        while (!this.isExhausted() && (findMatch(MATCH_TYPE.CHAR, currentChar) || findMatch(MATCH_TYPE.DIGIT, currentChar))) {
            lexemeValue.append(currentChar);
            currentChar = this.nextChar();
        }

        token = reserved_words.get(lexemeValue.toString());

        return new Lexeme(
            (token != null) ? token : LexemeToken.IDENTIFIER,
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
            //System.out.println("before: " + this.cursor);
            this.cursorForward();
            //System.out.println("after: " + this.cursor);
        }
        this.cursorBack();

        return new Lexeme(
            LexemeToken.NUMBER,
            Double.valueOf(lexemeValue.toString()),
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
        this.nextChar();
      }

      return new Lexeme(
          LexemeToken.STRING,
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
          this.moveCursor(1);
          if (this.isExhausted() || (!this.getCurrentCharacter().contains("\n"))) {
            this.moveCursor(1);
          }
          return true;
        }

        return false;
    }

    public void cursorBack() {
        this.cursor -= 1;
        this.column_number -= 1;
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
        regex_patterns.put(MATCH_TYPE.DOT, Pattern.compile("[.]"));
        regex_patterns.put(MATCH_TYPE.WHITESPACE, Pattern.compile("\\s"));
    }

    private static void load_reserved() {
        reserved_words.put("let", LexemeToken.KEYWORD_LET);
        reserved_words.put("if", LexemeToken.KEYWORD_IF);
        reserved_words.put("and", LexemeToken.KEYWORD_AND);
        reserved_words.put("func", LexemeToken.KEYWORD_FUNC);
    }

    private static boolean findMatch(MATCH_TYPE matchType, String toFind) {
        return regex_patterns.get(matchType).matcher(toFind).find();
    }
}
