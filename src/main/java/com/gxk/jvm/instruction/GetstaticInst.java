package com.gxk.jvm.instruction;

import com.gxk.jvm.rtda.Frame;
import com.gxk.jvm.rtda.Slot;
import com.gxk.jvm.rtda.heap.Heap;
import com.gxk.jvm.rtda.heap.KClass;
import com.gxk.jvm.rtda.heap.KField;
import com.gxk.jvm.rtda.heap.KMethod;

public class GetstaticInst implements Instruction {
  public final String clazz;
  public final String fieldName;
  public final String fieldDescriptor;

  @Override
  public int offset() {
    return 3;
  }

  public GetstaticInst(String clazz, String fieldName, String fieldDescriptor) {
    this.clazz = clazz;
    this.fieldName = fieldName;
    this.fieldDescriptor = fieldDescriptor;
  }


  @Override
  public void execute(Frame frame) {
    if (clazz.equals("java/lang/System")) {
      return;
    }

    KClass kClass = Heap.findClass(clazz);
    if (!kClass.isStaticInit()) {
      KMethod cinit = kClass.getMethod("<clinit>", "()V");
      if (cinit == null) {
        throw new IllegalStateException();
      }

      Frame newFrame = new Frame(cinit, frame.thread);
      newFrame.setOnPop(() -> kClass.setStaticInit(true));
      frame.thread.pushFrame(newFrame);

      frame.nextPc = frame.thread.getPc();
      return;
    }

    KField field = kClass.getField(fieldName, fieldDescriptor);
    if (field == null) {
      throw new IllegalStateException();
    }

    Slot val = field.val;
    if (val.ref != null) {
      frame.operandStack.pushRef(val.ref);
    } else {
      frame.operandStack.pushInt(val.num);
    }
  }
}
