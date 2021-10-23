package vm;

import java.util.HashMap;
import java.util.Map;

public class InstructionTable {
  private HashMap<OP_NAME, Integer> op_codes = new HashMap<OP_NAME, Integer>();
  private HashMap<Integer, Instruction> op_instructions = new HashMap<Integer, Instruction>();

  public InstructionTable(HashMap<Integer, Instruction> instructions) {
    for (Map.Entry<Integer, Instruction> entry : instructions.entrySet()) {
      int opCode = entry.getKey();
      Instruction instruction = entry.getValue();
      this.op_codes.put(instruction.getOpName(), opCode);
      this.op_instructions.put(opCode, instruction);
    }
  }

  public Instruction getInstruction(int opCode) {
    return this.op_instructions.get(opCode);
  }

  public int getOpCode(OP_NAME opName) {
    return this.op_codes.get(opName);
  }
}
