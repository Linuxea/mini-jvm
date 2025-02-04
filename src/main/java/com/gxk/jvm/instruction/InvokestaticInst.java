package com.gxk.jvm.instruction;

import com.gxk.jvm.rtda.Frame;
import com.gxk.jvm.rtda.heap.Heap;
import com.gxk.jvm.rtda.heap.KMethod;

public class InvokestaticInst implements Instruction {

  public final String methodName;
  public final String descriptor;

  public InvokestaticInst(String methodName, String descriptor) {
    this.methodName = methodName;
    this.descriptor = descriptor;
  }

  @Override
  public int offset() {
    return 3;
  }

  @Override
  public void execute(Frame frame) {
    KMethod method = Heap.findMethod(methodName, descriptor);
    Frame newFrame = new Frame(method, frame.thread);

    if (descriptor.startsWith("(I)")) {
      Integer tmp = frame.thread.currentFrame().operandStack.popInt();
      newFrame.localVars.setInt(0, tmp);
    }

    if (descriptor.startsWith("(II)")) {
      Integer o2 = frame.thread.currentFrame().operandStack.popInt();
      Integer o1 = frame.thread.currentFrame().operandStack.popInt();

      newFrame.localVars.setInt(0, o1);
      newFrame.localVars.setInt(1, o2);
    }

    frame.thread.pushFrame(newFrame);
  }
}

