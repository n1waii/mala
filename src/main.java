package src;

import src.compiler.*;

class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("thisIsAnIdentifier = 123.32");
        while (!lexer.isExhausted()) {
            Lexeme lexeme = lexer.getLexeme();
            if (lexeme != null) {
                System.out.println(lexeme);
            }
            lexer.cursorForward();
        }
    }
}   