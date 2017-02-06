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
package net.nandgr.cba.callgraph.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.nandgr.cba.callgraph.model.Call;
import net.nandgr.cba.callgraph.model.MethodGraph;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvocationGraphImpl implements InvocationGraph<MethodGraph, Call> {

  private static final Logger logger = LoggerFactory.getLogger(InvocationGraphImpl.class);
  private final DirectedGraph<MethodGraph, DefaultEdge> directedGraph;

  public InvocationGraphImpl() {
    this.directedGraph = new DefaultDirectedGraph<MethodGraph, DefaultEdge>(DefaultEdge.class);
  }

  @Override
  public boolean contains(MethodGraph element) {
    return directedGraph.containsVertex(element);
  }

  @Override
  public void add(MethodGraph element, MethodGraph parent) {
    if (element.equals(parent)) {
      return;
    }
    directedGraph.addVertex(element);
    directedGraph.addVertex(parent);
    directedGraph.addEdge(parent, element);
    if (findCycles(element)) {
      directedGraph.removeEdge(parent, element);
    }
  }

  @Override
  public Collection<Call> pathsToParents(MethodGraph element) {
    Collection<Call> paths = new HashSet<>();
      logger.debug("Finding path to parents for: {} ", element);
    if (!directedGraph.containsVertex(element)) {
      logger.warn("Graph does not contain vertex: {} . Returning empty collection.", element);
      return paths;
    }
    Set<DefaultEdge> incomingEdges = directedGraph.incomingEdgesOf(element);
    if (incomingEdges.isEmpty()) {
      return paths;
    }
    for (DefaultEdge incomingEdge : incomingEdges) {
      MethodGraph father = directedGraph.getEdgeSource(incomingEdge);
      logger.debug("Finding path to parents, element={}, father={} . About to find paths from father", element, father);
      Call call = new Call(father, element);
      paths.add(call);
      paths.addAll(pathsToParents(father));
    }
    return paths;
  }

  @Override
  public boolean findCycles(MethodGraph vertex) {
    CycleDetector<MethodGraph,DefaultEdge> cycleDetector = new CycleDetector(directedGraph);
    return cycleDetector.detectCyclesContainingVertex(vertex);
  }
}
