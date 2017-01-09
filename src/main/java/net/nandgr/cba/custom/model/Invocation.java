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
package net.nandgr.cba.custom.model;

public class Invocation {

  private String owner;
  private Method method;
  private Method notFrom;
  private Method from;
  private Boolean report;

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Method getNotFrom() {
    return notFrom;
  }

  public void setNotFrom(Method notFrom) {
    this.notFrom = notFrom;
  }

  public Method getFrom() {
    return from;
  }

  public void setFrom(Method from) {
    this.from = from;
  }

  public Boolean isReport() {
    if (report == null) {
      return true;
    }
    return report;
  }

  public void setReport(Boolean report) {
    this.report = report;
  }

  @Override
  public String toString() {
    return "Invocation{" +
            "owner='" + owner + '\'' +
            ", method=" + method +
            ", notFrom=" + notFrom +
            ", report=" + report +
            '}';
  }
}
