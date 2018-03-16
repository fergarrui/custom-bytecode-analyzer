package rules;

import net.nandgr.cba.report.ReportItem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SuperClassTest extends AbstractTest {

  public SuperClassTest() {
    super("superclass");
  }

  @Test
  public void test() {
    runTests();
    ReportItem reportItem0 = getReportItems().get(0);
    assertEquals("SuperClassTestFile$B.class", reportItem0.getClassName());
    ReportItem reportItem1 = getReportItems().get(1);
    assertEquals("SuperClassTestFile$C.class", reportItem1.getClassName());
  }
}
