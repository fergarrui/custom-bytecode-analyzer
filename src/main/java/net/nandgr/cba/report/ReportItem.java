package net.nandgr.cba.report;

public class ReportItem {

  private String jarPath = "";
  private String className = "";
  private final String methodName;
  private final String ruleName;
  private final int lineNumber;

  public ReportItem(int lineNumber, String methodName, String ruleName) {
    this.lineNumber = lineNumber;
    this.methodName = methodName;
    this.ruleName = ruleName;
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

  public void setJarPath(String jarPath) {
    this.jarPath = jarPath;
  }

  public void setClassName(String className) {
    this.className = className;
  }
}
