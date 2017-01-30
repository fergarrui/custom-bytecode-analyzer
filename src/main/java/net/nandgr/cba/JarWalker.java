/*
 * Copyright (c) 2016-2017, Fernando Garcia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.nandgr.cba;

import net.nandgr.cba.callgraph.model.Graph;
import net.nandgr.cba.cli.CliHelper;
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
    this.byteCodeAnalyzer = new CustomByteCodeAnalyzer(CliHelper.hasCustomFile(), CliHelper.hasChecks(), CliHelper.getRules());
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
      logger.info("Waiting for analysis... This may take a while");
      executorService.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      logger.error("Executor service interrupted. This shouldn't happen.", e);
      Thread.currentThread().interrupt();
    }
    logger.info("Grouping report items...");
    Map<String, List<ReportItem>> ruleNameReportItemsGrouped = groupReportItems(reportItemsFutureList);
    executorService.shutdownNow();
    logger.info("Walker shutdown.");
    logger.info("Building report...");
    ReportBuilder.saveAsHtml(ruleNameReportItemsGrouped);
    logger.info("Saving call graph...");
    Graph.saveCallGraph();
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
