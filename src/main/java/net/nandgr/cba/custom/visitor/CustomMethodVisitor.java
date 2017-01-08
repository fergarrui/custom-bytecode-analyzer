package net.nandgr.cba.custom.visitor;

import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodVisitor extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodVisitor.class);
  private final Method method;

  public CustomMethodVisitor(Method method, String ruleName) {
    super(ruleName);
    this.method = method;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    if (RuleHelper.isValidMethod(method, access, name, desc)) {
      ReportItem reportItem = new ReportItem(-1, name, getRuleName());
      if (showInReport()) {
        this.itemsFound.add(reportItem);
      }
      logger.debug("Issue found at method - access: {}, name: {}, desc: {}, signature: {}, exceptions: {}", access, name, desc, signature, exceptions);
      this.foundIssue = true;
    }
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  @Override
  public boolean showInReport() {
    return method.isReport();
  }
}
