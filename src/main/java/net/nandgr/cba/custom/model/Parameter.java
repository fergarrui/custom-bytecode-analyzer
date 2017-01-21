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

import java.util.List;

public class Parameter {

  private String type;
  private List<Annotation> annotations;
  private Boolean report;

  public String getType() {
    return type;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public Boolean isReport() {
    if (report == null) {
      return true;
    }
    return report;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  public Boolean getReport() {
    return report;
  }

  @Override
  public String toString() {
    return "Parameter{" +
            "type='" + type + '\'' +
            ", annotations=" + annotations +
            ", report=" + report +
            '}';
  }
}
