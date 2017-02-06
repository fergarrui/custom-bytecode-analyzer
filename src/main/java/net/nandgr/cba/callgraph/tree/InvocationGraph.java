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
import java.util.Set;
import net.nandgr.cba.callgraph.model.MethodGraph;

/**
 * This interface contains and manages all relations between the method invocations.
 * @param <T> Is the type of vertex (method)
 * @param <C> Is the relation between two vertex. Should represent a method invocation.
 */
public interface InvocationGraph<T, C> {

  /**
   * Standard contains method.
   * @param element element to find
   * @return a T element when found. Null of not found
   */
  boolean contains(T element);

  /**
   * Adds an element to a parent.
   * @param element
   */
  void add(T element, T parent);

  /**
   * @param element
   * @return A collection of C (invocations) from element to the beginning of the graph.
   */
  Collection<C> pathsToParents(T element);

  boolean findCycles(MethodGraph vertex);

}
