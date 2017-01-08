package net.nandgr.cba.custom.model;

import java.util.List;

public class Rule {

  private String name;
  private List<Invocation> invocations;
  private List<Method> methods;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Invocation> getInvocations() {
    return invocations;
  }

  public void setInvocations(List<Invocation> invocations) {
    this.invocations = invocations;
  }

  public List<Method> getMethods() {
    return methods;
  }

  public void setMethods(List<Method> methods) {
    this.methods = methods;
  }

  @Override
  public String toString() {
    return "Rule{" +
            "name='" + name + '\'' +
            ", invocations=" + invocations +
            ", methods=" + methods +
            '}';
  }
}
