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

public class Method {

  private String name;
  private String visibility;
  private String parameter;
  private Boolean report;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
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
    return "Method{" +
            "name='" + name + '\'' +
            ", visibility='" + visibility + '\'' +
            ", parameter='" + parameter + '\'' +
            ", report=" + report +
            '}';
  }
}
