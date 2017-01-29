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

public class CallGraph {

  private final MethodGraph caller;
  private final MethodGraph called;

  public CallGraph(MethodGraph caller, MethodGraph called) {
    this.caller = caller;
    this.called = called;
  }

  public MethodGraph getCaller() {
    return caller;
  }

  public MethodGraph getCalled() {
    return called;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CallGraph)) return false;

    CallGraph callGraph = (CallGraph) o;

    if (!getCaller().equals(callGraph.getCaller())) return false;
    return getCalled().equals(callGraph.getCalled());
  }

  @Override
  public int hashCode() {
    int result = getCaller().hashCode();
    result = 31 * result + getCalled().hashCode();
    return result;
  }
}
