package net.nandgr.cba.visitor.checks;

import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.visitor.checks.util.SerializationHelper;
import net.nandgr.cba.custom.visitor.CustomAbstractVisitor;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomDeserializationCheck extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomDeserializationCheck.class);

  public CustomDeserializationCheck() {
    super("CustomDeserializationCheck");
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    if (SerializationHelper.isCustomDeserializationMethod(access, name, desc)) {
      ReportItem reportItem = new ReportItem(-1, name, getRuleName());
      this.foundIssue = true;
      this.itemsFound.add(reportItem);
      logger.debug("Issue found at method - access: {}, name: {}, desc: {}, signature: {}, exceptions: {}", access, name, desc, signature, exceptions);
    }
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  @Override
  public boolean showInReport() {
    return true;
  }
}
