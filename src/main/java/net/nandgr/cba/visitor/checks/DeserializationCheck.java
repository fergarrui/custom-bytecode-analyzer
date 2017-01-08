package net.nandgr.cba.visitor.checks;

import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.visitor.CustomAbstractVisitor;
import net.nandgr.cba.custom.visitor.CustomInvocationFinderVisitor;
import net.nandgr.cba.visitor.checks.util.SerializationHelper;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeserializationCheck extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(DeserializationCheck.class);

  private final CustomInvocationFinderVisitor methodVisitor;
  private static final String READ_OBJECT_OWNER = "java/io/ObjectInputStream";
  private static final String READ_OBJECT_NAME = "readObject";
  private static final String READ_OBJECT_DESCRIPTOR = "java/lang/Object";

  public DeserializationCheck() {
    super("DeserializationCheck");
    Invocation invocation = new Invocation();
    invocation.setOwner(READ_OBJECT_OWNER);
    Method method = new Method();
    method.setName(READ_OBJECT_NAME);
    method.setParameter(READ_OBJECT_DESCRIPTOR);
    invocation.setMethod(method);
    this.methodVisitor = new CustomInvocationFinderVisitor(this, invocation);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    if (SerializationHelper.isCustomDeserializationMethod(access, name, desc)) {
      return super.visitMethod(access, name, desc, signature, exceptions);
    }
    return methodVisitor;
  }

  @Override
  public boolean showInReport() {
    return true;
  }
}
