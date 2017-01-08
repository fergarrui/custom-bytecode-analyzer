package net.nandgr.cba.custom.model;

public class Invocation {

  private String owner;
  private Method method;
  private Method notFrom;
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
