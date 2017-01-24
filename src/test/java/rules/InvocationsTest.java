package rules;

import java.util.List;
import net.nandgr.cba.report.ReportItem;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvocationsTest extends AbstractTest {

  public InvocationsTest() {
    super("invocations");
  }

  @Test
  public void test() {

    runTests();

    List<ReportItem> reportItems1 = getReportItems("invocations1");
    assertEquals(3, reportItems1.size());
    assertTrue(matchClassName(reportItems1, "InvocationsTestFile$A.class"));
    assertTrue(matchClassName(reportItems1, "InvocationsTestFile$B.class"));
    assertTrue(matchClassName(reportItems1, "InvocationsTestFile$D.class"));

    List<ReportItem> reportItems2 = getReportItems("invocations2");
    assertEquals(2, reportItems2.size());
    assertTrue(matchClassName(reportItems2, "InvocationsTestFile$B.class"));
    assertTrue(matchClassName(reportItems2, "InvocationsTestFile$D.class"));

    List<ReportItem> reportItems3 = getReportItems("invocations3");
    assertEquals(4, reportItems3.size());
    assertTrue(matchClassName(reportItems3, "InvocationsTestFile$A.class"));
    assertTrue(matchClassName(reportItems3, "InvocationsTestFile$B.class"));

    List<ReportItem> reportItems4 = getReportItems("invocations4");
    assertEquals(2, reportItems4.size());
    assertTrue(matchClassName(reportItems4, "InvocationsTestFile$A.class"));
    assertTrue(matchClassName(reportItems4, "InvocationsTestFile$B.class"));

    List<ReportItem> reportItems5 = getReportItems("invocations5");
    assertEquals(1, reportItems5.size());
    assertTrue(matchClassName(reportItems5, "InvocationsTestFile$B.class"));
  }
}
