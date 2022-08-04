package compiler.parser;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;

import compiler.Lexer;
import compiler.Lexeme;
import compiler.LexemeToken;

public class Parser {
  enum OperatorAssociativity {
    RIGHT, LEFT
  }

  private Lexer lexer;

  private static HashMap<LexemeToken, Boolean> expression_valids = new HashMap<LexemeToken, Boolean>();
  private static HashMap<LexemeToken, Integer> op_precedences = new HashMap<LexemeToken, Integer>();
  private static HashMap<LexemeToken, OperatorAssociativity> op_associativity = new HashMap<LexemeToken, OperatorAssociativity>();

  public Parser(Lexer lexer) {
      this.lexer = lexer;
      setup_expression_valids();
      setup_op_precedences();
      setup_op_associativities();
  }

  private static void throwError(Lexeme lexeme, String errorMessage) {
    throw new Error("FileName:" + lexeme.getLine() + ":" + lexeme.getColumn() + "error: " + errorMessage);
  }

  private Lexeme expect(LexemeToken token) {
    Lexeme lexeme = this.lexer.getCurrentLexeme();
    if (lexeme.getToken() != token) {
      throwError(lexeme, String.format("Expected token of type %s, instead got %s", token.toString(), lexeme.getToken().toString()));
    } else {
      this.lexer.getNextLexeme();
    }
    return lexeme;
  }

  private IASTNode parse_expression(boolean asArgument) {
    // this is going to follow the shunting yard algorithm

    Stack<Lexeme> operators = new Stack<Lexeme>();
    Stack<IASTNode> operands = new Stack<IASTNode>();

    // while there is a lexeme to read and the lexeme is a valid expression operator
    // used to differentiate between expressions assigned to variables and function arguments
    // f(1+2+x,(3+1))
    // x = 2 + 1 + z
    // y = 2
    scanner: while (
      this.lexer.getCurrentLexeme().getToken() != LexemeToken.EOF
        && ((asArgument && this.lexer.getCurrentLexeme().getToken() != LexemeToken.COMMA)
        || (expression_valids.containsKey(this.lexer.getCurrentLexeme().getToken())))
      ) {
      Lexeme lexeme = this.lexer.getCurrentLexeme();
      switch (lexeme.getToken()) {
		// add identifier
        case NUMBER:
          operands.push(new NumLiteralNode((Double) lexeme.getValue()));
          this.lexer.getNextLexeme();
          break;
        case LEFT_PAREN:
          operators.push(lexeme);
          this.lexer.getNextLexeme();
          break;
        case RIGHT_PAREN:
          while (!operators.empty() && operators.peek().getToken() != LexemeToken.LEFT_PAREN) {
            operands.push(new BinExpNode((String) operators.pop().getValue(), operands.pop(), operands.pop()));
          }

          if (operators.empty()) {
              if (asArgument) {
                break scanner;
              } else {
                throwError(lexeme, "Mismatched parenthesis");
              }
          }

          this.lexer.getNextLexeme();
          break;
        default:
          Lexeme op1 = lexeme;
          //System.out.println(op1);
          Integer op1Precedence = op_precedences.get(op1.getToken());
          if (op1Precedence != null) {
            while (!operators.empty() && (op_precedences.containsKey(operators.peek().getToken()))) {
              Lexeme op2 = operators.peek();
              Integer op2Precedence = op_precedences.get(op2.getToken());
              if ((op_associativity.get(op1.getToken()) == OperatorAssociativity.LEFT && op1Precedence == op2Precedence) || op2Precedence > op1Precedence) {
                operands.push(new BinExpNode((String) operators.pop().getValue(), operands.pop(), operands.pop()));
              } else {
                break;
              }
            }
            operators.push(op1);
            this.lexer.getNextLexeme();
          }
          break;
      }
    }

    while (!operators.empty()) {
      // note: check for mismatched parenthesis(if its a function call, there should be one extra parenthesis)
      /*
      Lexeme operator = operators.pop();
      if (operators.empty()) {
        // if its empty, 'operator' is the last operator and if its a function call, we expect a right parenthesis
        if (operator.getToken() == LexemeToken.RIGHT_PAREN) {

        }
      }
      */
      operands.push(new BinExpNode((String) operators.pop().getValue(), operands.pop(), operands.pop()));
    }

    // note: check for remaining operands
    return operands.pop();
  }

  private IASTNode parse_variable_definition() {
    //Lexeme keyword = this.lexer.getCurrentLexeme();
    this.expect(LexemeToken.KEYWORD_LET);
    Lexeme varName = this.expect(LexemeToken.IDENTIFIER);
    this.expect(LexemeToken.ASSIGNMENT_OP);
    IASTNode varValue = this.parse_expression(false);
    return new LocalVarDefNode((String) varName.getValue(), varValue);
  }

  private IASTNode parse_function_call() {
    // f(1+2 * f, 3, 5, 2+5)
    // let x = 1 + 2 * 3

    Lexeme functionName = this.lexer.getLastLexeme();
    ArrayList<IASTNode> arguments = new ArrayList<IASTNode>();
    this.expect(LexemeToken.LEFT_PAREN);

    if (this.lexer.getCurrentLexeme().getToken() == LexemeToken.RIGHT_PAREN) {
      // function call with no argument
      return new FunctionCallNode((String) functionName.getValue(), null);
    } else {
      // parse first expression argument
      arguments.add(this.parse_expression(true));
    }

    // function calls with multiple arguments
    while (this.lexer.getCurrentLexeme() != null && this.lexer.getCurrentLexeme().getToken() == LexemeToken.COMMA) {
      this.expect(LexemeToken.COMMA);
      arguments.add(this.parse_expression(true));
      Lexeme currentLexeme = this.lexer.getCurrentLexeme();
      if (currentLexeme != null) {
        if (currentLexeme.getToken() != LexemeToken.COMMA) {
          // end of function call
          break;
        }
      }
    }

    this.expect(LexemeToken.RIGHT_PAREN);
    return new FunctionCallNode((String) functionName.getValue(), arguments);
  }

  public ProgramNode parse() {
      ArrayList<IASTNode> body = new ArrayList<IASTNode>();

      this.lexer.getNextLexeme();
      while (!this.lexer.isExhausted()) {
        Lexeme lexeme = this.lexer.getCurrentLexeme();
        switch (lexeme.getToken()) {
          case KEYWORD_LET:
            body.add(this.parse_variable_definition());
            break;
          case IDENTIFIER:
            Lexeme next = this.lexer.getNextLexeme();
            if (next != null) {
              if (next.getToken() == LexemeToken.LEFT_PAREN) {
                body.add(this.parse_function_call());
                this.lexer.getNextLexeme();
              } else if (next.getToken() == LexemeToken.ASSIGNMENT_OP) {
                //body.add(this.parse_variable_assignment())
              } else {
                throwError(next, "Unexpected token");
              }
            } else {
              throwError(lexeme, "Unexpected token");
            }
            break;
            
          default: break;
        }
      }

      return new ProgramNode(body);
  }

  private static void setup_expression_valids() {
    expression_valids.put(LexemeToken.BINARY_MUL, true);
    expression_valids.put(LexemeToken.BINARY_DIV, true);
    expression_valids.put(LexemeToken.BINARY_ADD, true);
    expression_valids.put(LexemeToken.BINARY_SUB, true);
    expression_valids.put(LexemeToken.LEFT_PAREN, true);
    expression_valids.put(LexemeToken.RIGHT_PAREN, true);
    expression_valids.put(LexemeToken.NUMBER, true);
    expression_valids.put(LexemeToken.IDENTIFIER, true);
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
