// implements shunting yard algorithm

package compiler.parser;

import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import compiler.Lexer;
import compiler.Lexeme;
import compiler.LexemeToken;

public class Parser {
  enum OperatorAssociativity {
    RIGHT, LEFT
  }

  private Lexer lexer;
  private static HashMap<LexemeToken, Integer> op_precedences = new HashMap<LexemeToken, Integer>();
  private static HashMap<LexemeToken, OperatorAssociativity> op_associativity = new HashMap<LexemeToken, OperatorAssociativity>();

  public Parser(Lexer lexer) {
      this.lexer = lexer;
      setup_op_precedences();
      setup_op_associativities();
  }

  IASTNode Parse() {
    // this is going to follow the shunting yard algorithm
    // however, it will return one AST (PROGRAM) node that can be traversed

    List<Lexeme> output = new ArrayList<Lexeme>();
    Stack<IASTNode> operators = new Stack<IASTNode>();
    Stack<IASTNode> operands = new Stack<IASTNode>();

    while (!this.lexer.isExhausted()) {
      Lexeme lexeme = this.lexer.getNextLexeme();
      switch (lexeme.getToken()) {
        case NUMBER:
          operands.push(new NumLiteralNode((Integer) lexeme.getValue()));
        default:
          break;
      }
    }

    return new BinExpNode("+", null, null); // just so linter doesnt show errors for now
  }

  private static void setup_op_precedences() {
    op_precedences.put(LexemeToken.BINARY_MUL, 3);
    op_precedences.put(LexemeToken.BINARY_DIV, 3);
    op_precedences.put(LexemeToken.BINARY_ADD, 2);
    op_precedences.put(LexemeToken.BINARY_SUB, 2);
  }

  private static void setup_op_associativities() {
    op_associativity.put(LexemeToken.BINARY_MUL, OperatorAssociativity.LEFT);
    op_associativity.put(LexemeToken.BINARY_DIV, OperatorAssociativity.LEFT);
    op_associativity.put(LexemeToken.BINARY_ADD, OperatorAssociativity.LEFT);
    op_associativity.put(LexemeToken.BINARY_SUB, OperatorAssociativity.LEFT);
  }
}
