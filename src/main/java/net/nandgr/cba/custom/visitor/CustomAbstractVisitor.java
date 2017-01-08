package net.nandgr.cba.custom.visitor;

import net.nandgr.cba.report.ReportItem;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public abstract class CustomAbstractVisitor extends ClassVisitor implements CustomVisitor {

  protected List<ReportItem> itemsFound = new ArrayList<>();
  protected boolean foundIssue = false;
  private final String ruleName;

  public CustomAbstractVisitor(String ruleName) {
    super(Opcodes.ASM4);
    this.ruleName = ruleName;
  }

  @Override
  public boolean issueFound() {
    return foundIssue;
  }

  @Override
  public void setIssueFound(boolean issueFound) {
    this.foundIssue = issueFound;
  }

  @Override
  public List<ReportItem> itemsFound() {
    return itemsFound;
  }

  @Override
  public String getRuleName() {
    return this.ruleName;
  }

  @Override
  public void clear() {
    this.itemsFound.clear();
    this.foundIssue = false;
  }
}
