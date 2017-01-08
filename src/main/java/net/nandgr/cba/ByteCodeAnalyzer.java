package net.nandgr.cba;

import net.nandgr.cba.report.ReportItem;
import java.io.InputStream;
import java.util.List;

@FunctionalInterface
public interface ByteCodeAnalyzer {
  public List<ReportItem> analyze(InputStream inputStream);
}
