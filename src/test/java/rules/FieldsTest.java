package rules;

import java.util.List;
import java.util.stream.Collectors;
import net.nandgr.cba.report.ReportItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FieldsTest extends AbstractTest {

  public FieldsTest() {
    super("fields");
  }

  @Test
  public void test() {
    runTests();

    List<ReportItem> reportItems1 = getReportItems("fields1");
    assertEquals(2, reportItems1.size());
    assertTrue(matchClassName(reportItems1, "FieldsTestFile$C.class"));
    assertTrue(matchClassName(reportItems1, "FieldsTestFile$A.class"));

    List<ReportItem> reportItems2 = getReportItems("fields2").stream()
            .filter(r ->!r.getProperties().getOrDefault("Field name", "").contains("this"))
            .collect(Collectors.toList());
    assertEquals(8, reportItems2.size());

    List<ReportItem> reportItems3 = getReportItems("fields3");
    assertEquals(1, reportItems3.size());
    assertTrue(matchClassName(reportItems3, "FieldsTestFile$C.class"));

    List<ReportItem> reportItems4 = getReportItems("fields4");
    assertEquals(1, reportItems4.size());
    assertTrue(matchClassName(reportItems4, "FieldsTestFile$B.class"));

    List<ReportItem> reportItems5 = getReportItems("fields5");
    assertEquals(0, reportItems5.size());
  }
}
