package net.nandgr.cba.visitor.checks.util;

import org.objectweb.asm.Opcodes;

public class SerializationHelper {

  private static final String READ_OBJECT_ARGUMENT = "java/io/ObjectInputStream";
  private static final String READ_OBJECT_NAME = "readObject";

  private SerializationHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }

  public static boolean isCustomDeserializationMethod(int access, String name, String desc) {
    return access == Opcodes.ACC_PRIVATE && READ_OBJECT_NAME.equals(name) && desc.contains(READ_OBJECT_ARGUMENT);
  }
}
