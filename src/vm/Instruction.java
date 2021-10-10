package vm;

enum op_name {
    PUSH, ADD, HALT
}

interface op_handler {
  void run(MalaVM vm, int operand);
}

public class Instruction {
  private int op_code;
  private op_name name;
  private op_handler handler;

  public Instruction(int op_code, op_name name, op_handler handler) {
      this.op_code = op_code;
      this.name = name;
      this.handler = handler;
  }

  public int getOpCode() {
    return this.op_code;
  }

  public op_name getOpName() {
    return this.name;
  }

  public void execute(MalaVM vm, int operand) {
      this.handler.run(vm, operand);
  }
}
