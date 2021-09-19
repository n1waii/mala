package src;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import compiler.*;

class Main {
    public static void main(String[] args) throws IOException {
        String fileContents = new String(Files.readAllBytes(Paths.get("src/HelloWorld.mala")));
        Lexer lexer = new Lexer(fileContents);
        while (!lexer.isExhausted()) {
            Lexeme lexeme = lexer.getLexeme();
            if (lexeme != null) {
                System.out.println(lexeme);
            }
            lexer.cursorForward();
        }
    }
}
