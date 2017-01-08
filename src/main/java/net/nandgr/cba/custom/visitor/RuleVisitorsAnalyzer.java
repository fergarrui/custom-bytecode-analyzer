package net.nandgr.cba.custom.visitor;

import net.nandgr.cba.report.ReportItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public class RuleVisitorsAnalyzer {

  private final List<CustomVisitor> visitorList = new ArrayList<>();

  public List<CustomVisitor> getVisitorList() {
    return visitorList;
  }

  public List<ReportItem> runRules(InputStream inputStream) throws IOException {
    boolean meetsAllVisitors = true;
    List<ReportItem> reportItems = new ArrayList<>();
    ClassReader classReader = new ClassReader(inputStream);
    for (CustomVisitor customVisitor : visitorList) {
      classReader.accept((ClassVisitor) customVisitor,0);
      meetsAllVisitors &= customVisitor.issueFound();
      if (!meetsAllVisitors) {
        return new ArrayList<>();
      }
      reportItems.addAll(customVisitor.itemsFound());
      customVisitor.clear();
    }
    return reportItems;
  }
}
