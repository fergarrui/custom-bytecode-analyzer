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

public class ReportItem {

  private String jarPath = "";
  private String className = "";
  private final String methodName;
  private final String fieldName;
  private final String ruleName;
  private final int lineNumber;

  public ReportItem(int lineNumber, String methodName, String fieldName, String ruleName) {
    this.lineNumber = lineNumber;
    this.methodName = methodName;
    this.ruleName = ruleName;
    this.fieldName = fieldName;
  }

  public String getJarPath() {
    return jarPath;
  }

  public String getClassName() {
    return className;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public String getRuleName() {
    return ruleName;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setJarPath(String jarPath) {
    this.jarPath = jarPath;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}
