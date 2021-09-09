import compiler.*;

class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("""
            thisIsAnIdentifier = 0123
        """);
        Lexeme test = lexer.getLexeme();
        //System.out.println();
    }
}