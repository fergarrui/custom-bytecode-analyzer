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

public class Rule {

  private String name;
  private List<String> interfaces;
  private String superClass;
  private List<Invocation> invocations;
  private List<Method> methods;
  private List<Annotation> annotations;
  private List<Field> fields;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getInterfaces() {
    return interfaces;
  }

  public void setInterfaces(List<String> interfaces) {
    this.interfaces = interfaces;
  }

  public String getSuperClass() {
    return superClass;
  }

  public void setSuperClass(String superClass) {
    this.superClass = superClass;
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

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  @Override
  public String toString() {
    return "Rule{" +
            "name='" + name + '\'' +
            ", interfaces=" + interfaces +
            ", superClass='" + superClass + '\'' +
            ", invocations=" + invocations +
            ", methods=" + methods +
            ", annotations=" + annotations +
            ", fields=" + fields +
            '}';
  }
}
