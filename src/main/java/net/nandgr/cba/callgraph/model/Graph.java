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
package net.nandgr.cba.callgraph.model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import net.nandgr.cba.cli.CliHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Graph {

  private static final Logger logger = LoggerFactory.getLogger(Graph.class);
  private static final Collection<CallGraph> graphCollection = Collections.synchronizedSet(new HashSet<>());
  private static final String CALL_GRAPH_FILE_NAME = "call-graph.dot";

  public static void addCallGraph(CallGraph callGraph) {
    graphCollection.add(callGraph);
  }

  public static void saveCallGraph() {
    File callGraphFile = new File(getCallGraphFileName());
    try {
      File parentFile = callGraphFile.getParentFile();
      if (!parentFile.exists()) {
        parentFile.mkdirs();
      }
      callGraphFile.createNewFile();
      String graphText = buildTextFromGraph();
      FileUtils.writeStringToFile(callGraphFile, graphText);
      logger.info("Call graph written at: {}", callGraphFile.getAbsoluteFile());
    } catch (IOException e) {
      logger.error("Error when creating call graph file.", e);
    }
  }

  private static String buildTextFromGraph() {
    StringBuilder stringBuilder = new StringBuilder("graph callGraph {" + System.lineSeparator());
    for (CallGraph callGraph : graphCollection) {
      MethodGraph caller = callGraph.getCaller();
      MethodGraph called = callGraph.getCalled();
      stringBuilder.append("\"").append(caller.getOwner()).append(":").append(caller.getName()).append("\"")
              .append(" -- ").append("\"").append(called.getOwner()).append(":").append(called.getName()).append("\"").append(System.lineSeparator());
    }
    stringBuilder.append("}").append(System.lineSeparator());
    return stringBuilder.toString();
  }

  private static String getCallGraphFileName() {
    String callGraphFileName = CliHelper.getOutputDir();
    if (!callGraphFileName.endsWith(File.separator)) {
      callGraphFileName += File.separator;
    }
    callGraphFileName += CALL_GRAPH_FILE_NAME;
    return callGraphFileName;
  }
}
