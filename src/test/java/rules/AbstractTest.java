package rules;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.nandgr.cba.ByteCodeAnalyzer;
import net.nandgr.cba.CustomByteCodeAnalyzer;
import net.nandgr.cba.JarAnalyzerCallable;
import net.nandgr.cba.custom.model.Rules;
import net.nandgr.cba.report.ReportItem;
import org.apache.commons.io.IOUtils;

public abstract class AbstractTest {

  private final String RELATIVE_PATH = "testfiles";
  private final String RESOURCES_PATH = "/rulejson";
  private final String PATH;
  private List<ReportItem> reportItems = new ArrayList<>();

  public AbstractTest(String path) {
    this.PATH = path;
  }

  protected void runTests() {
    this.reportItems.clear();
    try {
      Rules rules = getRules();
      ByteCodeAnalyzer byteCodeAnalyzer = new CustomByteCodeAnalyzer(true, false, rules);
      Files.walk(Paths.get(getPath()))
              .filter(filePath -> filePath.toUri().toString().endsWith(".class"))
              .forEach(filePath -> {
                try {
                  InputStream inputStream = Files.newInputStream(filePath);
                  List<ReportItem> reportItems = byteCodeAnalyzer.analyze(inputStream);
                  JarAnalyzerCallable.addContextToReportItems(reportItems, "test", filePath.getFileName().toString(), null);
                  this.reportItems.addAll(reportItems);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected List<ReportItem> getReportItems() {
    return reportItems;
  }

  protected List<ReportItem> getReportItems(String ruleName) {
    return getReportItems().stream()
            .filter(reportItem -> reportItem.getRuleName().equals(ruleName))
            .collect(Collectors.toList());
  }

  protected boolean matchClassName(List<ReportItem> reportItems, String className) {
    return reportItems.stream().
            anyMatch( r -> r.getClassName().equals(className));
  }

  protected Rules getRules() throws IOException {
    String json = IOUtils.toString(this.getClass().getResourceAsStream(getJsonPath()));
    Gson gson = new Gson();
    Rules rules = gson.fromJson(json, Rules.class);
    return rules;
  }

  private String getJsonPath() {
    return RESOURCES_PATH + File.separator + this.getClass().getSimpleName() + ".json";
  }

  private String getPath() {
    URL url = getClass().getResource(RELATIVE_PATH + File.separator + PATH);
    return url.getPath();
  }
}
