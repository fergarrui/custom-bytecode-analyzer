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

import net.nandgr.cba.callgraph.runnable.ClassCallGraph;
import net.nandgr.cba.decompile.Decompiler;
import net.nandgr.cba.decompile.ZipEntryDecompiler;
import net.nandgr.cba.report.ReportItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
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
            String zipEntryName = zipEntry.getName();
            logger.debug("Class found: {}", zipEntryName);

            // retrieving a new inputStream - do not extract variable
            ClassReader classReader = new ClassReader(zipFile.getInputStream(zipEntry));
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode,0);
            List<ReportItem> analyzeReportItems = byteCodeAnalyzer.analyze(classNode);

            ClassCallGraph classCallGraph = new ClassCallGraph(classNode);
            classCallGraph.populateClassGraph();

            String decompiledFile = null;
            if (!analyzeReportItems.isEmpty()) {
              Decompiler decompiler = new ZipEntryDecompiler();
              // retrieving a new inputStream - do not extract variable
              decompiledFile = decompiler.decompile(zipFile.getInputStream(zipEntry), zipEntryName);
            }

            addContextToReportItems(analyzeReportItems, jarPath.toAbsolutePath().toString(), zipEntryName, decompiledFile);
            reportItems.addAll(analyzeReportItems);
          } catch (IOException e) {
            logger.error("Error while analyzing Jar internals.", e);
          }
        });
      zipFile.close();

    return reportItems;
  }

  public static void addContextToReportItems(List<ReportItem> reportItems, String jarPath, String className, String decompiledFile) {
    reportItems.stream().forEach(reportItem -> {
      reportItem.setJarPath(jarPath);
      reportItem.setClassName(className);
      reportItem.setDecompiledFile(decompiledFile);
    });
  }

  private static boolean isClassFile(ZipEntry zipEntry) {
    return zipEntry.getName().endsWith(CLASS_EXTENSION);
  }
}
