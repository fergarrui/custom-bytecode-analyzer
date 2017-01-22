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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReportItem {

  private String jarPath = "";
  private String className = "";
  private File decompiledFile;
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

  public File getDecompiledFile() {
    return decompiledFile;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public ReportItem addProperty(String propertyName, String propertyValue) {
    properties.put(propertyName, propertyValue);
    return this;
  }

  public void setDecompiledFile(File decompiledFile) {
    this.decompiledFile = decompiledFile;
  }
}
