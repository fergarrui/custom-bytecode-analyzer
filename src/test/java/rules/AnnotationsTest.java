package rules;

import java.util.List;
import net.nandgr.cba.report.ReportItem;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnnotationsTest extends AbstractTest {

  public AnnotationsTest() {
    super("annotations");
  }

  @Test
  public void test(){

    runTests();

    List<ReportItem> reportItems1 = getReportItems("annotations1");
    assertEquals(3, reportItems1.size());
    assertTrue(matchClassName(reportItems1, "AnnotationsTestFile$A.class"));
    assertTrue(matchClassName(reportItems1, "AnnotationsTestFile$E.class"));
    assertTrue(matchClassName(reportItems1, "AnnotationsTestFile$G.class"));

    List<ReportItem> reportItems2 = getReportItems("annotations2");
    assertEquals(2, reportItems2.size());
    assertTrue(matchClassName(reportItems2, "AnnotationsTestFile$A.class"));

    List<ReportItem> reportItems3 = getReportItems("annotations3");
    assertEquals(2, reportItems3.size());
    assertTrue(matchClassName(reportItems3, "AnnotationsTestFile$G.class"));

    List<ReportItem> reportItems4 = getReportItems("annotations4");
    assertEquals(2, reportItems4.size());
    assertTrue(matchClassName(reportItems4, "AnnotationsTestFile$D.class"));
    assertTrue(matchClassName(reportItems4, "AnnotationsTestFile$G.class"));

    List<ReportItem> reportItems5 = getReportItems("annotations5");
    assertEquals(2, reportItems5.size());
    assertTrue(matchClassName(reportItems5, "AnnotationsTestFile$A.class"));
    assertTrue(matchClassName(reportItems5, "AnnotationsTestFile$D.class"));

  }
}
