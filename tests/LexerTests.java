import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

import compiler.Lexer;
import compiler.Lexeme;
import compiler.LexemeToken;

class LexerTests {
    private static boolean compareLexeme(Lexeme lexeme1, Lexeme lexeme2) {
      return ((lexeme1.getToken() == lexeme2.getToken()) &&
            (lexeme1.getLine() == lexeme2.getLine())
            && (lexeme1.getColumn() == lexeme2.getColumn())
            && (lexeme1.getValue().equals(lexeme2.getValue())));
    }

    @Test
    void shouldTokenizeExpressions() {
        Lexer lexer = new Lexer("1+(3*2)-1/2");
        ArrayList<Lexeme> gotLexemes = lexer.tokenizeAll();
        Lexeme[] expectedLexemes = {
            new Lexeme(LexemeToken.NUMBER, 1.0, 1, 1),
            new Lexeme(LexemeToken.BINARY_ADD, "+", 1, 2),
            new Lexeme(LexemeToken.LEFT_PAREN, "(", 1, 3),
            new Lexeme(LexemeToken.NUMBER, 3.0, 1, 4),
            new Lexeme(LexemeToken.BINARY_MUL, "*", 1, 5),
            new Lexeme(LexemeToken.NUMBER, 2.0, 1, 6),
            new Lexeme(LexemeToken.RIGHT_PAREN, ")", 1, 7),
            new Lexeme(LexemeToken.BINARY_SUB, "-", 1, 8),
            new Lexeme(LexemeToken.NUMBER, 1.0, 1, 9),
            new Lexeme(LexemeToken.BINARY_DIV, "/", 1, 10),
            new Lexeme(LexemeToken.NUMBER, 2.0, 1, 11),
            new Lexeme(LexemeToken.EOF, "eof", 1, 12),
        };

        for (int i = 0; i < gotLexemes.size(); i++) {
          Assertions.assertTrue(compareLexeme(gotLexemes.get(i), expectedLexemes[i]));
        }

    }

    @Test
    void shouldTokenizeVariableDef() {
        Lexer lexer = new Lexer("let x = 1 + 2");
        ArrayList<Lexeme> gotLexemes = lexer.tokenizeAll();
        Lexeme[] expectedLexemes = {
            new Lexeme(LexemeToken.KEYWORD_LET, "let", 1, 1),
            new Lexeme(LexemeToken.IDENTIFIER, "x", 1, 5),
            new Lexeme(LexemeToken.ASSIGNMENT_OP, "=", 1, 7),
            new Lexeme(LexemeToken.NUMBER, 1.0, 1, 9),
            new Lexeme(LexemeToken.BINARY_ADD, "+", 1, 11),
            new Lexeme(LexemeToken.NUMBER, 2.0, 1, 13),
            new Lexeme(LexemeToken.EOF, "eof", 1, 14),
        };

        for (int i = 0; i < gotLexemes.size(); i++) {
          System.out.println(i + " | " + gotLexemes.get(i));
          Assertions.assertTrue(compareLexeme(gotLexemes.get(i), expectedLexemes[i]));
        }

    }

    @Test
    void shouldTokenizeFunctionCall() {
        Lexer lexer = new Lexer("foo(1, 2)");
        ArrayList<Lexeme> gotLexemes = lexer.tokenizeAll();
        Lexeme[] expectedLexemes = {
            new Lexeme(LexemeToken.IDENTIFIER, "foo", 1, 1),
            new Lexeme(LexemeToken.LEFT_PAREN, "(", 1, 4),
            new Lexeme(LexemeToken.NUMBER, 1.0, 1, 5),
            new Lexeme(LexemeToken.COMMA, ",", 1, 6),
            new Lexeme(LexemeToken.NUMBER, 2.0, 1, 8),
            new Lexeme(LexemeToken.RIGHT_PAREN, ")", 1, 9),
            new Lexeme(LexemeToken.EOF, "eof", 1, 10),
        };

        for (int i = 0; i < gotLexemes.size(); i++) {
          Assertions.assertTrue(compareLexeme(gotLexemes.get(i), expectedLexemes[i]));
        }

    }
}
