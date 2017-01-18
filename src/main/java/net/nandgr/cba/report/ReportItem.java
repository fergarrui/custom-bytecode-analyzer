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
  private final boolean showInReport;

  public ReportItem(int lineNumber, String methodName, String fieldName, String ruleName, boolean showInReport) {
    this.lineNumber = lineNumber;
    this.methodName = methodName;
    this.ruleName = ruleName;
    this.fieldName = fieldName;
    this.showInReport = showInReport;
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

  public boolean isShowInReport() {
    return showInReport;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReportItem)) return false;

    ReportItem that = (ReportItem) o;

    if (getLineNumber() != that.getLineNumber()) return false;
    if (isShowInReport() != that.isShowInReport()) return false;
    if (getJarPath() != null ? !getJarPath().equals(that.getJarPath()) : that.getJarPath() != null) return false;
    if (getClassName() != null ? !getClassName().equals(that.getClassName()) : that.getClassName() != null)
      return false;
    if (getMethodName() != null ? !getMethodName().equals(that.getMethodName()) : that.getMethodName() != null)
      return false;
    if (getFieldName() != null ? !getFieldName().equals(that.getFieldName()) : that.getFieldName() != null)
      return false;
    return getRuleName() != null ? getRuleName().equals(that.getRuleName()) : that.getRuleName() == null;
  }

  @Override
  public int hashCode() {
    int result = getJarPath() != null ? getJarPath().hashCode() : 0;
    result = 31 * result + (getClassName() != null ? getClassName().hashCode() : 0);
    result = 31 * result + (getMethodName() != null ? getMethodName().hashCode() : 0);
    result = 31 * result + (getFieldName() != null ? getFieldName().hashCode() : 0);
    result = 31 * result + (getRuleName() != null ? getRuleName().hashCode() : 0);
    result = 31 * result + getLineNumber();
    result = 31 * result + (isShowInReport() ? 1 : 0);
    return result;
  }
}
