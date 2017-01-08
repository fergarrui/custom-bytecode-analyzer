package net.nandgr.cba.custom.visitor;

import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomInvocationFinderVisitor extends MethodVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomInvocationFinderVisitor.class);

  private final CustomVisitor parentVisitor;
  private final Invocation methodInvocation;
  private int lineNumber;

  public CustomInvocationFinderVisitor(CustomVisitor parentVisitor, Invocation methodInvocation) {
    super(Opcodes.ASM4);
    this.parentVisitor = parentVisitor;
    this.methodInvocation = methodInvocation;
    this.lineNumber = -1;
  }

  @Override
  public void visitLineNumber(int line, Label start) {
    lineNumber = line;
    super.visitLineNumber(line, start);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    logger.trace("visitMethodInsn: opcode={} owner={} name={} desc={} itf={}", opcode, owner, name, desc, itf);
    if (RuleHelper.isValidMethodInvocation(methodInvocation, owner, name, desc)) {
      ReportItem reportItem = new ReportItem(lineNumber, name, parentVisitor.getRuleName());
      if (parentVisitor.showInReport()) {
        parentVisitor.itemsFound().add(reportItem);
      }
      logger.debug("Match found at method invocation - opcode: {}, owner: {}, name: {}, desc: {}, itf: {}", opcode,owner, name, desc);
      parentVisitor.setIssueFound(true);
    }
    super.visitMethodInsn(opcode, owner, name, desc, itf);
  }
}
