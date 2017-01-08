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
