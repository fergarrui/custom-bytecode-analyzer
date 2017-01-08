package net.nandgr.cba;

import net.nandgr.cba.report.ReportBuilder;
import net.nandgr.cba.report.ReportItem;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarWalker {

  private static final Logger logger = LoggerFactory.getLogger(JarWalker.class);
  private static final String JAR_EXTENSION = ".jar";
  private final String path;
  private final ExecutorService executorService;
  private final ByteCodeAnalyzer byteCodeAnalyzer;

  public JarWalker(String path, int maxThreads) throws ReflectiveOperationException, IOException {
    this.path = path;
    this.executorService  = Executors.newFixedThreadPool(maxThreads);
    this.byteCodeAnalyzer = new CustomByteCodeAnalyzer();
  }

  public void walk() throws IOException {
    logger.info("Walking through JAR files in: " + path);
    List<Future<List<ReportItem>>> reportItemsFutureList = new ArrayList<>();
    Files.walk(Paths.get(path))
            .filter(filePath -> filePath.toUri().toString().endsWith(JAR_EXTENSION))
            .forEach(jarPath -> {
              logger.info("Analyzing: " + jarPath.toUri().toString());
              JarAnalyzerCallable jarAnalyzerCallable = new JarAnalyzerCallable(jarPath, byteCodeAnalyzer);
              Future<List<ReportItem>> reportItemsFuture = executorService.submit(jarAnalyzerCallable);
              reportItemsFutureList.add(reportItemsFuture);
            });
    logger.info("Shutting down walker...");
    executorService.shutdown();
    try {
      executorService.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      logger.error("Executor service interrupted. This shouldn't happen.", e);
      Thread.currentThread().interrupt();
    }
    executorService.shutdownNow();
    logger.info("Walker shutdown.");
    logger.info("Building report...");
    Map<String, List<ReportItem>> ruleNameReportItemsGrouped = groupReportItems(reportItemsFutureList);
    ReportBuilder.saveAsHtml(ruleNameReportItemsGrouped);
  }

  private Map<String, List<ReportItem>> groupReportItems(List<Future<List<ReportItem>>> reportItemsFutureList) {
    Map<String, List<ReportItem>> groupedItems = new HashMap<>();
    reportItemsFutureList.stream().forEach(futureList -> {
      try {
        List<ReportItem> reportItems = futureList.get();
        reportItems.stream().forEach(reportItem -> {
          String ruleName = reportItem.getRuleName();
          if(groupedItems.containsKey(ruleName)) {
            groupedItems.get(ruleName).add(reportItem);
          } else {
            List<ReportItem> reportItemsValue = new ArrayList<>();
            reportItemsValue.add(reportItem);
            groupedItems.put(ruleName, reportItemsValue);
          }
        });
      } catch (ExecutionException | InterruptedException e) {
        logger.error("Error while building the report. Still analyzers alive?", e);
      }
    });
    return groupedItems;
  }
}
