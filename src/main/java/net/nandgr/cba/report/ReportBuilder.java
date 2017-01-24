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
package net.nandgr.cba.report;

import com.google.common.collect.Lists;
import java.io.StringWriter;
import java.util.Properties;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.cli.CliHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportBuilder {

  private static final Logger logger = LoggerFactory.getLogger(ReportBuilder.class);

  private ReportBuilder() {
    throw new IllegalAccessError("Do not instantiate this class.");
  }

  public static void saveAsHtml(Map<String, List<ReportItem>> reportItems) {
    for (Map.Entry<String,List<ReportItem>> entry : reportItems.entrySet()) {
      String ruleName = entry.getKey();
      saveAsHtml(ruleName,entry.getValue());
    }
  }

  public static void saveAsHtml(String ruleName, List<ReportItem> reportItemList) {
    File reportsDirectory = new File(CliHelper.getOutputDir());
    if (!reportsDirectory.exists()) {
      reportsDirectory.mkdir();
    }
    int reportFileIndex = 0;
    try {
      List<String> htmlChunks = generateHtmlChunks(reportItemList);
      for (String htmlChunk : htmlChunks) {
        File reportFile = new File(reportsDirectory.getAbsolutePath() + "/" + StringsHelper.spacesToDashesLowercase(ruleName) + "-"+ reportFileIndex +".html");
        reportFile.createNewFile();
        FileUtils.writeStringToFile(reportFile, htmlChunk);
        reportFileIndex++;
      }
      logger.info(reportItemList.size() + " issue(s) found for \"" + ruleName + "\". Report created in: " + reportsDirectory.getAbsolutePath());
    } catch (IOException e) {
      logger.error("Error when saving the report.", e);
    }
  }

  private static List<String> generateHtmlChunks(List<ReportItem> reportItemList) {
    List<String> htmlChunks = new ArrayList<>();

    VelocityEngine velocityEngine = new VelocityEngine();
    Properties p = new Properties();
    p.setProperty("resource.loader", "class");
    p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.init(p);
    Template template = velocityEngine.getTemplate("template/report_template.html");

    int maxItemsInReport = CliHelper.getMaxItemsInReport();
    List<List<ReportItem>> reportItemsChunks = Lists.partition(reportItemList, maxItemsInReport);

    for (List<ReportItem> reportItemsChunk : reportItemsChunks ) {
      VelocityContext velocityContext = new VelocityContext();
      velocityContext.put("jarPath", CliHelper.getPathToAnalyze());
      velocityContext.put("ruleName", reportItemsChunk.get(0).getRuleName());
      velocityContext.put("reportItems", reportItemsChunk);

      StringWriter stringWriter = new StringWriter();
      template.merge(velocityContext, stringWriter);
      htmlChunks.add(stringWriter.toString());
    }
    return htmlChunks;
  }
}
