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
package net.nandgr.cba.custom.visitor;

import java.util.ArrayList;
import java.util.List;
import net.nandgr.cba.custom.model.Annotation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.model.Parameter;
import net.nandgr.cba.custom.model.Variable;
import net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor;
import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.LocalVariableAnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodVisitor extends CustomAbstractClassVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodVisitor.class);
  private final Method method;

  public CustomMethodVisitor(Method method, String ruleName) {
    super(ruleName);
    this.method = method;
  }

  @Override
  public void process() {
    for (MethodNode methodNode : getClassNode().methods) {
      int access = methodNode.access;
      String name = methodNode.name;
      String desc = methodNode.desc;
      String signature = methodNode.signature;
      List<String> exceptions = methodNode.exceptions;

      logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
      List<Annotation> annotations = method.getAnnotations();
      boolean annotationFound = true;
      if (annotations != null && !annotations.isEmpty()) {
        annotationFound = isMethodAnnotationFound(annotations, methodNode);
      }
      List<Variable> variables = method.getVariables();
      boolean variableFound = true;
      boolean variableAnnotationFound = true;
      if (variables != null && !variables.isEmpty()) {
        for (Variable ruleVariable : variables) {
          variableFound &= RuleHelper.containsVariable(ruleVariable, methodNode.localVariables);
          List<Annotation> variableAnnotations = ruleVariable.getAnnotations();
          if (variableAnnotations !=null && !variableAnnotations.isEmpty()) {
            variableAnnotationFound &= isLocalVariableAnnotationFound(variableAnnotations, methodNode);
          }
        }
      }
      boolean parameterFound = true;
      boolean parameterAnnotationFound = true;
      List<Parameter> parameters = method.getParameters();
      if (parameters != null && !parameters.isEmpty()) {
        for (Parameter parameterRule : parameters) {
          parameterFound &= RuleHelper.containsParameter(parameterRule, desc);
          List<Annotation> parameterAnnotations = parameterRule.getAnnotations();
          parameterAnnotationFound &= isParameterAnnotationFound(parameterAnnotations, methodNode);
        }
      }

      if (RuleHelper.isValidMethod(method, access, name) && annotationFound && variableFound && parameterFound && variableAnnotationFound && parameterAnnotationFound) {
        ReportItem reportItem = new ReportItem(-1, name, null, getRuleName(), showInReport());
        this.itemsFound.add(reportItem);
        logger.debug("Issue found at method - access: {}, name: {}, desc: {}, signature: {}, exceptions: {}", access, name, desc, signature, exceptions);
        this.foundIssue = true;
      }
    }
  }

  private boolean isParameterAnnotationFound(List<Annotation> annotationRules, MethodNode methodNode) {
    boolean annotationFound = true;
    List<AnnotationNode> allParameterAnnotations = new ArrayList<>();
    List<AnnotationNode>[] visibleParameterAnnotations = methodNode.visibleParameterAnnotations;
    if (visibleParameterAnnotations != null && visibleParameterAnnotations.length != 0) {
      addIfNotNull(allParameterAnnotations, visibleParameterAnnotations);
    }
    List<AnnotationNode>[] inVisibleParameterAnnotations = methodNode.invisibleParameterAnnotations;
    if (inVisibleParameterAnnotations != null && inVisibleParameterAnnotations.length != 0) {
      addIfNotNull(allParameterAnnotations, inVisibleParameterAnnotations);
    }
    if (annotationRules != null && !annotationRules.isEmpty()) {
      for (Annotation annotationRule : annotationRules) {
        annotationFound &= RuleHelper.containsAnnotation(annotationRule, allParameterAnnotations);
      }
    }

    return annotationFound;
  }

  private void addIfNotNull(List<AnnotationNode> allParameterAnnotations, List<AnnotationNode>[] visibleParameterAnnotations) {
    for (List<AnnotationNode> visibleAnnotations : visibleParameterAnnotations) {
      if (visibleAnnotations != null) {
        allParameterAnnotations.addAll(visibleAnnotations);
      }
    }
  }

  private boolean isLocalVariableAnnotationFound(List<Annotation> annotationRules, MethodNode methodNode) {
    boolean annotationFound = true;
    List<AnnotationNode> allLocalVariableAnnotations = new ArrayList<>();
    List<LocalVariableAnnotationNode> invisibleVariableAnnotations = methodNode.invisibleLocalVariableAnnotations;
    if (invisibleVariableAnnotations != null) {
      allLocalVariableAnnotations.addAll(invisibleVariableAnnotations);
    }
    List<LocalVariableAnnotationNode> visibleVariableAnnotations = methodNode.visibleLocalVariableAnnotations;
    if (visibleVariableAnnotations != null) {
      allLocalVariableAnnotations.addAll(visibleVariableAnnotations);
    }
    for (Annotation annotationRule : annotationRules) {
      annotationFound &= RuleHelper.containsAnnotation(annotationRule, allLocalVariableAnnotations);
    }
    return annotationFound;
  }

  private boolean isMethodAnnotationFound(List<Annotation> annotationRules, MethodNode methodNode) {
    boolean annotationFound = true;
    List<AnnotationNode> allMethodAnnotations = new ArrayList<>();
    List<AnnotationNode> invisibleAnnotations = methodNode.invisibleAnnotations;
    if (invisibleAnnotations != null) {
      allMethodAnnotations.addAll(invisibleAnnotations);
    }
    List<AnnotationNode> visibleAnnotations = methodNode.visibleAnnotations;
    if (visibleAnnotations != null) {
      allMethodAnnotations.addAll(visibleAnnotations);
    }
    for (Annotation annotationRule : annotationRules) {
      annotationFound &= RuleHelper.containsAnnotation(annotationRule, allMethodAnnotations);
    }
    return annotationFound;
  }

  @Override
  public boolean showInReport() {
    return method.isReport();
  }
}
