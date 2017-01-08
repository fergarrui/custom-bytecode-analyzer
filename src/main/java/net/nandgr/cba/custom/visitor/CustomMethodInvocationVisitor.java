package net.nandgr.cba.custom.visitor;

import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodInvocationVisitor extends CustomAbstractVisitor {

  private final Invocation invocation;

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodInvocationVisitor.class);

  public CustomMethodInvocationVisitor(Invocation invocation, String ruleName) {
    super(ruleName);
    this.invocation = invocation;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);

    Method notFrom = invocation.getNotFrom();
    if (checkNotFrom(notFrom, access, name, desc)) {
      return new CustomInvocationFinderVisitor(this, invocation);
    }
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  private static boolean checkNotFrom(Method notFrom, int access, String name, String desc) {
    if (notFrom == null) {
      return true;
    }
    return !RuleHelper.isValidMethod(notFrom, access, name, desc);
  }

  @Override
  public boolean showInReport() {
    return invocation.isReport();
  }
}
