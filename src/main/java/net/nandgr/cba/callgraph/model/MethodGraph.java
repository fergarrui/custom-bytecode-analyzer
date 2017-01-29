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

public class MethodGraph {

  private final String owner;
  private final String name;

  public MethodGraph(String owner, String name) {
    this.owner = owner;
    this.name = name;
  }

  public String getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MethodGraph)) return false;

    MethodGraph that = (MethodGraph) o;

    if (!getOwner().equals(that.getOwner())) return false;
    return getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    int result = getOwner().hashCode();
    result = 31 * result + getName().hashCode();
    return result;
  }
}
