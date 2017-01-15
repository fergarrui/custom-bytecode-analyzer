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

import com.webfirmframework.wffweb.tag.html.Body;
import com.webfirmframework.wffweb.tag.html.H4;
import com.webfirmframework.wffweb.tag.html.Html;
import com.webfirmframework.wffweb.tag.html.P;
import com.webfirmframework.wffweb.tag.html.TitleTag;
import com.webfirmframework.wffweb.tag.html.attribute.CellSpacing;
import com.webfirmframework.wffweb.tag.html.metainfo.Head;
import com.webfirmframework.wffweb.tag.html.tables.Table;
import com.webfirmframework.wffweb.tag.html.tables.Td;
import com.webfirmframework.wffweb.tag.html.tables.Tr;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.cli.CliHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
    } catch (IOException | ParserConfigurationException e) {
      logger.error("Error when saving the report.", e);
    }
  }

  private static List<String> generateHtmlChunks(List<ReportItem> reportItemList) throws ParserConfigurationException {
    Iterator<ReportItem> reportItemIterator = reportItemList.iterator();
    List<String> htmlChunks = new ArrayList<>();
    if (!reportItemIterator.hasNext()) {
      htmlChunks.add(createEmptyReport());
    }
    int reportItemsCounter = 0;
    Html html = null;
    Body body = null;
    int maxItemsInReport = CliHelper.getMaxItemsInReport();
    while(reportItemIterator.hasNext()) {
      if (reportItemsCounter % maxItemsInReport == 0) {
        if (reportItemsCounter != 0) {
          htmlChunks.add(html.toHtmlString());
        }
        html = new Html(null);
        html.setPrependDocType(true);
        Head head = new Head(html);
        TitleTag title = new TitleTag(head);
        new NoTag(title, "Report for: " + CliHelper.getPathToAnalyze());
        body = new Body(html);
      }
      ReportItem reportItem = reportItemIterator.next();
      H4 itemTittle = new H4(body);
      new NoTag(itemTittle, reportItem.getRuleName());
      Table itemTable = new Table(body, new CellSpacing(10));

      Tr jarName = new Tr(itemTable);
      Td jarNameKey = new Td(jarName);
      new NoTag(jarNameKey, "Jar name: ");
      Td jarNameValue = new Td(jarName);
      new NoTag(jarNameValue, reportItem.getJarPath());

      Tr className = new Tr(itemTable);
      Td classNameKey = new Td(className);
      new NoTag(classNameKey, "Class name: ");
      Td classNameValue = new Td(className);
      new NoTag(classNameValue, reportItem.getClassName());

      int reportItemLineNumber = reportItem.getLineNumber();
      if (reportItemLineNumber != -1) {
        Tr lineNumber = new Tr(itemTable);
        Td lineNumberKey = new Td(lineNumber);
        new NoTag(lineNumberKey, "Line: ");
        Td lineNumberValue = new Td(lineNumber);
        new NoTag(lineNumberValue, String.valueOf(reportItemLineNumber));
      }
      String reportMethodName = reportItem.getMethodName();
      if (!StringUtils.isBlank(reportMethodName)) {
        Tr methodName = new Tr(itemTable);
        Td methodNameKey = new Td(methodName);
        new NoTag(methodNameKey, "Method name: ");
        Td methodNameValue = new Td(methodName);
        new NoTag(methodNameValue, reportMethodName);
      }
      String reportFieldName = reportItem.getFieldName();
      if (!StringUtils.isBlank(reportFieldName)) {
        Tr fieldName = new Tr(itemTable);
        Td fieldNameKey = new Td(fieldName);
        new NoTag(fieldNameKey, "Field name: ");
        Td fieldNameValue = new Td(fieldName);
        new NoTag(fieldNameValue, reportFieldName);
      }
      if (!reportItemIterator.hasNext()) {
        htmlChunks.add(html.toHtmlString());
      }
      reportItemsCounter++;
    }
    return htmlChunks;
  }

  private static String createEmptyReport() {
    Html html = new Html(null);
    html.setPrependDocType(true);
    Head head = new Head(html);
    TitleTag title = new TitleTag(head);
    new NoTag(title, "Report for: " + CliHelper.getPathToAnalyze());
    Body body = new Body(html);
    P p = new P(body);
    new NoTag(p, "No issues found.");
    return html.toHtmlString();
    }
}
