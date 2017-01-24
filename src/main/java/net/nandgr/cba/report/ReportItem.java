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
package net.nandgr.cba.report;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;

public class ReportItem {

  private String jarPath = "";
  private String className = "";
  private String decompiledFile;
  private final String ruleName;
  private final boolean showInReport;
  private Map<String,String> properties = new HashMap<>();

  public ReportItem(String ruleName, boolean showInReport) {
    this.ruleName = ruleName;
    this.showInReport = showInReport;
  }

  public String getJarPath() {
    return jarPath;
  }

  public String getClassName() {
    return className;
  }

  public String getRuleName() {
    return ruleName;
  }

  public void setJarPath(String jarPath) {
    this.jarPath = jarPath;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public boolean isShowInReport() {
    return showInReport;
  }

  public String getDecompiledFile() {
    return decompiledFile;
  }

  public String getDecompiledFileHtml() {
    return StringEscapeUtils.escapeHtml(decompiledFile);
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public ReportItem addProperty(String propertyName, String propertyValue) {
    properties.put(propertyName, propertyValue);
    return this;
  }

  public void setDecompiledFile(String decompiledFile) {
    this.decompiledFile = decompiledFile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReportItem)) return false;

    ReportItem that = (ReportItem) o;

    if (isShowInReport() != that.isShowInReport()) return false;
    if (getJarPath() != null ? !getJarPath().equals(that.getJarPath()) : that.getJarPath() != null) return false;
    if (getClassName() != null ? !getClassName().equals(that.getClassName()) : that.getClassName() != null)
      return false;
    if (getDecompiledFile() != null ? !getDecompiledFile().equals(that.getDecompiledFile()) : that.getDecompiledFile() != null)
      return false;
    if (getRuleName() != null ? !getRuleName().equals(that.getRuleName()) : that.getRuleName() != null) return false;
    return getProperties() != null ? getProperties().equals(that.getProperties()) : that.getProperties() == null;
  }

  @Override
  public int hashCode() {
    int result = getJarPath() != null ? getJarPath().hashCode() : 0;
    result = 31 * result + (getClassName() != null ? getClassName().hashCode() : 0);
    result = 31 * result + (getDecompiledFile() != null ? getDecompiledFile().hashCode() : 0);
    result = 31 * result + (getRuleName() != null ? getRuleName().hashCode() : 0);
    result = 31 * result + (isShowInReport() ? 1 : 0);
    result = 31 * result + (getProperties() != null ? getProperties().hashCode() : 0);
    return result;
  }
}
