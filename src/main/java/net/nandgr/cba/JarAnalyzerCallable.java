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

import net.nandgr.cba.report.ReportItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarAnalyzerCallable implements Callable {

  private static final Logger logger = LoggerFactory.getLogger(JarAnalyzerCallable.class);

  private static final String CLASS_EXTENSION = ".class";
  private final Path jarPath;
  private final ByteCodeAnalyzer byteCodeAnalyzer;

  public JarAnalyzerCallable(Path jarPath, ByteCodeAnalyzer byteCodeAnalyzer) {
    this.jarPath = jarPath;
    this.byteCodeAnalyzer = byteCodeAnalyzer;
  }

  @Override
  public List<ReportItem> call() throws Exception {
    File jarFile = new File(jarPath.toUri());
    List<ReportItem> reportItems = new ArrayList<>();
      ZipFile zipFile = new ZipFile(jarFile);
      zipFile.stream().filter(JarAnalyzerCallable::isClassFile)
        .forEach(zipEntry -> {
          try {
            logger.debug("Class found: {}", zipEntry.getName());
            List<ReportItem> analyzeReportItems = byteCodeAnalyzer.analyze(zipFile.getInputStream(zipEntry));
            addContextToReportItems(analyzeReportItems, jarPath.toAbsolutePath().toString(), zipEntry.getName());
            reportItems.addAll(analyzeReportItems);
          } catch (IOException e) {
            logger.error("Error while analyzing Jar internals.", e);
          }
        });
      zipFile.close();
    return reportItems;
  }

  private static void addContextToReportItems(List<ReportItem> reportItems, String jarPath, String className) {
    reportItems.stream().forEach(reportItem -> {
      reportItem.setJarPath(jarPath);
      reportItem.setClassName(className);
    });
  }

  private static boolean isClassFile(ZipEntry zipEntry) {
    return zipEntry.getName().endsWith(CLASS_EXTENSION);
  }
}
