package rules;

import java.util.List;
import net.nandgr.cba.report.ReportItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InterfacesTest extends AbstractTest {

  public InterfacesTest() {
    super("interfaces");
  }

  @Test
  public void test() {
    runTests();

    List<ReportItem> reportItems1 = getReportItems("interfaces1");
    assertEquals(3, reportItems1.size());
    assertTrue(matchClassName(reportItems1, "InterfacesTestFile$A.class"));
    assertTrue(matchClassName(reportItems1, "InterfacesTestFile$D.class"));
    assertTrue(matchClassName(reportItems1, "InterfacesTestFile$E.class"));

    List<ReportItem> reportItems2 = getReportItems("interfaces2");
    assertEquals(1, reportItems2.size());
    assertTrue(matchClassName(reportItems2, "InterfacesTestFile$D.class"));

    List<ReportItem> reportItems3 = getReportItems("interfaces3");
    assertEquals(1, reportItems3.size());
    assertTrue(matchClassName(reportItems3, "InterfacesTestFile$D.class"));

    List<ReportItem> reportItems4 = getReportItems("interfaces4");
    assertEquals(0, reportItems4.size());
  }
}
