package net.nandgr.cba.custom.visitor;

import net.nandgr.cba.report.ReportItem;
import java.util.List;

public interface CustomVisitor {
  boolean issueFound();
  void setIssueFound(boolean issueFound);
  List<ReportItem> itemsFound();
  String getRuleName();
  boolean showInReport();
  void clear();
}
