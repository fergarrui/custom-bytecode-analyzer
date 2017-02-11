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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.nandgr.cba.callgraph.graph.InvocationGraph;
import net.nandgr.cba.callgraph.graph.InvocationGraphImpl;
import net.nandgr.cba.cli.CliHelper;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.report.ReportItem;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphHolder {

  private static final Logger logger = LoggerFactory.getLogger(GraphHolder.class);
  private static final Map<String, Collection<Call>> methodGraphMap = new HashMap<>();
  private static final Collection<Call> graphCollection = Collections.synchronizedSet(new HashSet<>());
  private static InvocationGraph<MethodGraph, Call> invocationGraph = new InvocationGraphImpl();

  public static void addCallGraph(Call call) {
    graphCollection.add(call);
  }

  public static void saveCallGraph() {
    for (Map.Entry<String, Collection<Call>> entry : methodGraphMap.entrySet()) {
      String ruleName = entry.getKey();
      Collection<Call> methodGraphs = entry.getValue();
      File callGraphFile = new File(getCallGraphFileName(ruleName));
      try {
        File parentFile = callGraphFile.getParentFile();
        if (!parentFile.exists()) {
          parentFile.mkdirs();
        }
        callGraphFile.createNewFile();
        String graphText = buildTextFromGraph(methodGraphs);
        FileUtils.writeStringToFile(callGraphFile, graphText);
        logger.info("Call graph written at: {}", callGraphFile.getAbsoluteFile());
      } catch (IOException e) {
        logger.error("Error when creating call graph file.", e);
      }
    }
  }

  private static String buildTextFromGraph(Collection<Call> calls) {
    StringBuilder stringBuilder = new StringBuilder("graph callGraph {" + System.lineSeparator());

    for (Call call : calls) {
      MethodGraph caller = call.getCaller();
      MethodGraph called = call.getCalled();

      String callerOwner = caller.getOwner();
      String callerName = caller.getName();

      stringBuilder.append("\"").append(callerOwner).append(":").append(callerName).append("\"");
        if (called != null) {
          String calledOwner = called.getOwner();
          String calledName = called.getName();
          stringBuilder.append(" -- ")
                    .append("\"").append(calledOwner).append(":").append(calledName).append("\"");
        }
        stringBuilder.append(System.lineSeparator());
    }

    stringBuilder.append("}").append(System.lineSeparator());
    return stringBuilder.toString();
  }

  // Not used at the moment. Will be configurable to create a full graph of the analyzed directory.
  private static String buildTextFromGraph() {
    StringBuilder stringBuilder = new StringBuilder("graph callGraph {" + System.lineSeparator());
    for (Call call : graphCollection) {
      MethodGraph caller = call.getCaller();
      MethodGraph called = call.getCalled();
      stringBuilder.append("\"").append(caller.getOwner()).append(":").append(caller.getName()).append("\"")
              .append(" -- ").append("\"").append(called.getOwner()).append(":").append(called.getName()).append("\"").append(System.lineSeparator());
    }
    stringBuilder.append("}").append(System.lineSeparator());
    return stringBuilder.toString();
  }

  private static String getCallGraphFileName(String ruleName) {
    String callGraphFileName = CliHelper.getOutputDir();
    if (!callGraphFileName.endsWith(File.separator)) {
      callGraphFileName += File.separator;
    }
    callGraphFileName += "call-graph-" + StringsHelper.spacesToDashesLowercase(ruleName) + ".dot";
    return callGraphFileName;
  }

  public static void createGraph() {
    logger.info("Creating call graph... May take a while.");
    long start = System.currentTimeMillis();
    for (Call call: graphCollection) {
      MethodGraph caller = call.getCaller();
      MethodGraph called = call.getCalled();
      invocationGraph.add(called, caller);
    }
    long end = System.currentTimeMillis();
    logger.info("Call graph created in {} ms.", (end-start));
  }

  public static void filterGraph(Map<String, List<ReportItem>> reportItemsMap) {
    logger.info("Filtering graph...");
    long start = System.currentTimeMillis();
    if (invocationGraph == null) {
      return;
    }
    for (Map.Entry<String, List<ReportItem>> entry : reportItemsMap.entrySet()) {
      String ruleName = entry.getKey();
      List<ReportItem> reportItems = entry.getValue();
      for (ReportItem reportItem : reportItems) {
        // TODO refactor to static string constant
        String methodNameKey = "Method Name";
        Map<String, String> reportItemProperties = reportItem.getProperties();
        if (reportItemProperties.containsKey(methodNameKey)) {

          String className = Type.getObjectType(reportItem.getClassName()).getClassName();
          String methodName = reportItemProperties.get(methodNameKey);
          MethodGraph methodGraph = new MethodGraph(StringsHelper.removeClassSuffix(className), methodName);
          Collection<Call> calls = invocationGraph.pathsToParents(methodGraph);
          if (methodGraphMap.containsKey(ruleName)) {
            Collection<Call> alreadyExistingCalls = methodGraphMap.get(ruleName);
            alreadyExistingCalls.addAll(calls);
          } else {
            methodGraphMap.put(ruleName, calls);
          }
        }
      }
    }
    long end = System.currentTimeMillis();
    logger.info("Graph filtered in {} ms.", (end-start));
  }
}
