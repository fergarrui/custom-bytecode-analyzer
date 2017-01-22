package rules;

import java.util.List;
import net.nandgr.cba.report.ReportItem;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodsTest extends AbstractTest {

  public MethodsTest() {
    super("methods");
  }

  @Test
  public void test(){

    runTests();

    List<ReportItem> reportItems1 = getReportItems("methods1");
    assertEquals(4, reportItems1.size()); // 3 + <init>
    assertTrue(matchClassName(reportItems1, "MethodsTestFile$A.class"));
    assertTrue(matchClassName(reportItems1, "MethodsTestFile$D.class"));
    assertTrue(matchClassName(reportItems1, "MethodsTestFile$E.class"));
    assertTrue(matchClassName(reportItems1, "MethodsTestFile.class"));

    List<ReportItem> reportItems2 = getReportItems("methods2");
    assertEquals(1, reportItems2.size());
    assertTrue(matchClassName(reportItems2, "MethodsTestFile$B.class"));

    List<ReportItem> reportItems3 = getReportItems("methods3");
    assertEquals(2, reportItems3.size()); // 1 report per variable
    assertTrue(matchClassName(reportItems3, "MethodsTestFile$C.class"));

    List<ReportItem> reportItems4 = getReportItems("methods4");
    assertEquals(1, reportItems4.size());
    assertTrue(matchClassName(reportItems4, "MethodsTestFile$F.class"));
  }
}
