package vm;

import java.util.ArrayList;

public class BytecodeStream {
  private ArrayList<Integer> stream = new ArrayList<Integer>();
  private ArrayList<Double> constants = new ArrayList<Double>();

  public void pushBytecode(int bin) {
    this.stream.add(bin);
  }

  public int pushConstant(double constantNumber) {
    this.constants.add(constantNumber);
    return this.constants.size()-1;
  }

  public double getConstant(int index) {
    return this.constants.get(index);
  }

  public int getBytecode(int index) {
    return this.stream.get(index);
  }
}
