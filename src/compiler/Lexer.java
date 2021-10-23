package compiler;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class Lexer {
    interface ITokenizerPredicate {
        boolean isTrue();
    }

    enum TokenizerType {
      CHAR_SEQUENCE, STRING_SEQUENCE, NUMBER_SEQUENCE
    }

    private String input;
    private int cursor;
    private Lexeme current_lexeme;
    private Lexeme last_lexeme;
    //private HashMap<Integer, Lexeme> lexeme_cache = new HashMap<Integer, Lexeme>();
    private int line_number = 1;
    private int column_number = 1;
    private static enum MATCH_TYPE {
        DIGIT, CHAR, QUOTATION, BACKSLASH,
        EQUAL, DOT, NEWLINE, TAB, WHITESPACE
    };
    private static HashMap<MATCH_TYPE, Pattern> regex_patterns = new HashMap<MATCH_TYPE, Pattern>();
    private static HashMap<String, LexemeToken> reserved_words = new HashMap<String, LexemeToken>();
    private HashMap<TokenizerType, ITokenizerPredicate> tokenizer_predicates = new HashMap<TokenizerType, ITokenizerPredicate>();

    public Lexer(String input) {
        this.input = input;
        this.cursor = 0;
        compile_regex();
        load_reserved();
        this.load_predicate_checks(); // must be non static for reference to object
    }

    public ArrayList<Lexeme> tokenizeAll() { // should only be used for unit testing
      ArrayList<Lexeme> lexemes = new ArrayList<Lexeme>();
      while (!this.isExhausted()) {
          lexemes.add(this.getNextLexeme());
      }
      return lexemes;
    }

    public Lexeme getNextLexeme() {
        this.last_lexeme = this.current_lexeme;
        Lexeme lexeme = this.findNewLexeme();
        this.current_lexeme = lexeme;
        return lexeme;
    }

    public Lexeme getCurrentLexeme() {
      return this.current_lexeme;
    }

    public Lexeme getLastLexeme() {
      return this.last_lexeme;
    }

    private String getCurrentCharacter() {
        if (this.isExhausted()) {
            return null;
        }
        return Character.toString(this.input.charAt(this.cursor));
    }

    private String peekNextChar() {
      if (this.cursor+1 <= this.input.length()) {
        return Character.toString(this.input.charAt(this.cursor+1));
      }

      return null;
    }

    private Lexeme findNewLexeme() {
        if (this.isExhausted()) {
            return new Lexeme(LexemeToken.EOF, "eof", this.getCursorLine(), this.getCursorColumn());
        }

        if (findMatch(MATCH_TYPE.WHITESPACE, this.getCurrentCharacter())) {
            while (!this.isExhausted() && findMatch(MATCH_TYPE.WHITESPACE, this.getCurrentCharacter())) {
              this.cursorForward();
            };

            if (this.isExhausted()) {
              return new Lexeme(LexemeToken.EOF, "eof", this.getCursorLine(), this.getCursorColumn());
            }
        }

        String currentChar = this.getCurrentCharacter();
        int currentLine = this.getCursorLine();
        int currentColumn = this.getCursorColumn();

        switch (currentChar) {
          case "+":
            this.cursorForward();
            return new Lexeme(LexemeToken.BINARY_ADD, "+", currentLine, currentColumn);
          case "-":
            this.cursorForward();
            return new Lexeme(LexemeToken.BINARY_SUB, "-", currentLine, currentColumn);
          case "*":
            this.cursorForward();
            return new Lexeme(LexemeToken.BINARY_MUL, "*", currentLine, currentColumn);
          case "/":
            this.cursorForward();
            return new Lexeme(LexemeToken.BINARY_DIV, "/", currentLine, currentColumn);
          case "(":
            this.cursorForward();
            return new Lexeme(LexemeToken.LEFT_PAREN, "(", currentLine, currentColumn);
          case ")":
            this.cursorForward();
            return new Lexeme(LexemeToken.RIGHT_PAREN, ")", currentLine, currentColumn);
          case ".":
            if (findMatch(MATCH_TYPE.DIGIT, this.peekNextChar())) {
              return this.findNumberSequence();
            }
            break;
          case ",":
            this.cursorForward();
            return new Lexeme(LexemeToken.COMMA, ",", currentLine, currentColumn);
          case "=":
            String nextChar = this.peekNextChar();
            if (nextChar != null && nextChar == "=") {
                this.moveCursor(2); // move it up twice to close the '==' token
                return new Lexeme(LexemeToken.COMP_EQUAL, "==", this.getCursorLine(), this.getCursorColumn()-2);
            } else {
                this.cursorForward();
                return new Lexeme(LexemeToken.ASSIGNMENT_OP, "=", this.getCursorLine(), this.getCursorColumn()-1);
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

        ITokenizerPredicate charSequencePredicate = this.tokenizer_predicates.get(TokenizerType.CHAR_SEQUENCE);
        StringBuilder lexemeValue = new StringBuilder();
        int lineStart = this.getCursorLine();
        int columnStart = this.getCursorColumn();
        LexemeToken token;

        do {
          lexemeValue.append(this.getCurrentCharacter());
          this.cursorForward();
        } while (charSequencePredicate.isTrue());

        token = reserved_words.get(lexemeValue.toString()); // see if its a reserved word(keyword)

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
        ITokenizerPredicate numberSequencePredicate = this.tokenizer_predicates.get(TokenizerType.NUMBER_SEQUENCE);
        StringBuilder lexemeValue = new StringBuilder();
        int lineStart = this.getCursorLine();
        int columnStart = this.getCursorColumn();

        do {
          lexemeValue.append(this.getCurrentCharacter());
          this.cursorForward();
        } while (numberSequencePredicate.isTrue());

        return new Lexeme(
            LexemeToken.NUMBER,
            Double.valueOf(lexemeValue.toString()),
            lineStart,
            columnStart
        );
    }

    private Lexeme findStringSequence() {
      ITokenizerPredicate stringSequencePredicate = this.tokenizer_predicates.get(TokenizerType.STRING_SEQUENCE);
      StringBuilder lexemeValue = new StringBuilder();
      int lineStart = this.getCursorLine();
      int columnStart = this.getCursorColumn();

      this.cursorForward();
      if (!stringSequencePredicate.isTrue()) {
        return null;
      }

      do {
        lexemeValue.append(this.getCurrentCharacter());
        this.cursorForward();
      } while (stringSequencePredicate.isTrue());

      this.cursorForward(); // move cursor up from last quoation mark

      return new Lexeme(
          LexemeToken.STRING,
          lexemeValue.toString().substring(0, lexemeValue.length()), // remove the last quotation mark
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
        reserved_words.put("function", LexemeToken.KEYWORD_FUNC);
    }

    private void load_predicate_checks() {
      Lexer lexer = this;
      ITokenizerPredicate charSequencePredicate = new ITokenizerPredicate() {
        public boolean isTrue() {
          return (!lexer.isExhausted() &&
              (findMatch(MATCH_TYPE.CHAR, lexer.getCurrentCharacter()) || findMatch(MATCH_TYPE.DIGIT, lexer.getCurrentCharacter())));
        }
      };

      ITokenizerPredicate stringSequencePredicate = new ITokenizerPredicate() {
        public boolean isTrue() {
          return (!lexer.isExhausted() && !findMatch(MATCH_TYPE.QUOTATION, lexer.getCurrentCharacter()));
        }
      };

      ITokenizerPredicate numberSequencePredicate = new ITokenizerPredicate() {
        public boolean isTrue() {
          return (!lexer.isExhausted() &&
              (findMatch(MATCH_TYPE.DIGIT, lexer.getCurrentCharacter()) || findMatch(MATCH_TYPE.DOT, lexer.getCurrentCharacter())));
        }
      };

      lexer.tokenizer_predicates.put(TokenizerType.CHAR_SEQUENCE, charSequencePredicate);
      lexer.tokenizer_predicates.put(TokenizerType.STRING_SEQUENCE, stringSequencePredicate);
      lexer.tokenizer_predicates.put(TokenizerType.NUMBER_SEQUENCE, numberSequencePredicate);
    }

    private static boolean findMatch(MATCH_TYPE matchType, String toFind) {
        return regex_patterns.get(matchType).matcher(toFind).find();
    }
}
