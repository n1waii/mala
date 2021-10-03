package compiler;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import compiler.parser.Parser;
import compiler.parser.IASTNode;

class Main {
    public static void main(String[] args) throws IOException {
        String fileContents = new String(Files.readAllBytes(Paths.get("src/HelloWorld.mala")));
        Lexer lexer = new Lexer(fileContents);
        Parser parser = new Parser(lexer);
        IASTNode program = parser.Parse();
        
    }
}
