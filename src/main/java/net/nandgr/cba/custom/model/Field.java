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

public class Field {

  private String visibility;
  private String type;
  private String valueRegex;
  private String nameRegex;
  private boolean report;

  public String getVisibility() {
    return visibility;
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValueRegex() {
    return valueRegex;
  }

  public void setValueRegex(String valueRegex) {
    this.valueRegex = valueRegex;
  }

  public String getNameRegex() {
    return nameRegex;
  }

  public void setNameRegex(String nameRegex) {
    this.nameRegex = nameRegex;
  }

  public boolean isReport() {
    return report;
  }

  public void setReport(boolean report) {
    this.report = report;
  }

  @Override
  public String toString() {
    return "Field{" +
            "visibility='" + visibility + '\'' +
            ", type='" + type + '\'' +
            ", valueRegex='" + valueRegex + '\'' +
            ", nameRegex='" + nameRegex + '\'' +
            ", report=" + report +
            '}';
  }
}
