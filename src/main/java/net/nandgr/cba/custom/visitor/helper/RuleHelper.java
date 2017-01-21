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
package net.nandgr.cba.custom.visitor.helper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.nandgr.cba.custom.model.Annotation;
import net.nandgr.cba.custom.model.Field;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.model.Parameter;
import net.nandgr.cba.custom.model.Variable;
import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleHelper {

  private static final Logger logger = LoggerFactory.getLogger(RuleHelper.class);

  private RuleHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }


  public static boolean allEmpty(Method method) {
    if (method == null) {
      return false;
    }
    return StringUtils.isBlank(method.getName()) && method.getParameters() == null && StringUtils.isBlank(method.getVisibility());
  }

  public static boolean isValidMethod(Method method, int access, String name) {
    boolean isValid = true;
    String methodVisibility = method.getVisibility();
    if (methodVisibility != null) {
      int ruleVisibility = getOpcodeVisibility(methodVisibility);
      isValid &=  (access & ruleVisibility) != 0;
    }
    String methodName = method.getName();
    if (methodName != null) {
      isValid &= methodName.equals(name);
    }
    logger.trace("isValidMethod: {} - method={}, access={}, name={}", isValid, method, access, name );
    return isValid;
  }

  public static boolean isValidMethodInvocation(Invocation invocation, String owner, String name, String desc) {
    boolean isValid = true;
    Method invocationMethod = invocation.getMethod();
    if (invocationMethod != null) {
      isValid &= isValidMethod(invocationMethod, 0, name );
    }
    String invocationOwner = invocation.getOwner();
    if (invocationOwner != null) {
      isValid &= owner.equals(StringsHelper.dotsToSlashes(invocationOwner));
    }
    logger.trace("isValidMethodInvocation: {} - invocation={}, owner={}, name={}, desc={}", isValid, invocation, owner, name, desc);
    return isValid;
  }

  public static boolean isValidField(Field field, int access, String name, String desc, String signature, Object value) {
    logger.trace("isValidField: field={}, access={}, name={}, desc={}, signature={}, value={}", field, access, name, desc, signature, value);
    boolean isValid = true;
    String type = field.getType();
    if (!StringUtils.isBlank(type)) {
      isValid &= StringsHelper.dotsToSlashes(type).equals(StringsHelper.simpleDescriptorToHuman(desc));
    }
    String visibility = field.getVisibility();
    if (!StringUtils.isBlank(visibility)) {
      int visibilityOpcode = getOpcodeVisibility(visibility);
      isValid &= (access & visibilityOpcode) != 0;
    }
    String valueRegex = field.getValueRegex();
    if (!StringUtils.isBlank(valueRegex)) {
      try {
        Pattern pattern = Pattern.compile(valueRegex);
        String valueString = String.valueOf(value);
        Matcher matcher = pattern.matcher(valueString);
        isValid &= matcher.matches();
      } catch (PatternSyntaxException e) {
        throw e;
      }
    }
    String nameRegex = field.getNameRegex();
    if (!StringUtils.isBlank(nameRegex)) {
      try {
        Pattern pattern = Pattern.compile(nameRegex);
        String nameString = String.valueOf(name);
        Matcher matcher = pattern.matcher(nameString);
        isValid &= matcher.matches();
      } catch (PatternSyntaxException e) {
        throw e;
      }
    }
    logger.trace("isValidField : {} - field={}, access={}, name={}, desc={}, signature={}, value={}", isValid, field, access, name, desc, signature, value);
    return isValid;
  }

  public static boolean containsAnnotation(Annotation annotationRule, List<AnnotationNode> annotationNodes) {
    for (AnnotationNode annotationNode : annotationNodes) {
      String desc = annotationNode.desc;
      if (StringsHelper.simpleDescriptorToHuman(desc).equals(StringsHelper.dotsToSlashes(annotationRule.getType()))) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsParameter(Parameter parameterRule, String desc) {
    Type[] parameters = Type.getArgumentTypes(desc);
    logger.trace("containsParameter: parameterRule={}, desc={}");
    if (parameters == null) {
      return false;
    }
    for (Type parameter : parameters) {
      logger.trace("parameter={}", parameter.getClassName());
      if(parameterRule.getType() == null || parameterRule.getType().equals(parameter.getClassName())) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsVariable(Variable variableRule, List<LocalVariableNode> variableNodes) {
    logger.trace("containsVariable: variableRule={}, variableNodes={}", variableRule, variableNodes);
    if (variableNodes == null) {
      return false;
    }
    for (LocalVariableNode variableNode : variableNodes) {
      String name = variableNode.name;
      String desc = variableNode.desc;
      if (!name.contains("this") && isValidVariable(variableRule, name, desc)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isValidVariable(Variable variable, String name, String desc) {
    logger.trace("isValidVariable : variable={}, name={}, desc={}", variable, name, desc);
    boolean isValid = true;
    String type = variable.getType();
    if (!StringUtils.isBlank(type)) {
      isValid &= desc.contains(StringsHelper.dotsToSlashes(variable.getType()));
    }
    String nameRegex = variable.getNameRegex();
    if (!StringUtils.isBlank(nameRegex)) {
      try {
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(name);
        isValid &= matcher.matches();
      } catch (PatternSyntaxException e) {
        throw e;
      }
    }
    return isValid;
  }

  public static int getOpcodeVisibility(String visibility) {
    if ("public".equals(visibility)) {
      return Opcodes.ACC_PUBLIC;
    }
    if ("protected".equals(visibility)) {
      return Opcodes.ACC_PROTECTED;
    }
    if ("private".equals(visibility)) {
      return Opcodes.ACC_PRIVATE;
    }
    return 0;
  }

  public static boolean checkNotFrom(Method notFrom, int access, String name) {
    if (notFrom == null) {
      return true;
    }
    return !RuleHelper.isValidMethod(notFrom, access, name);
  }

  public static boolean checkFrom(Method from, int access, String name) {
    if (from == null) {
      return true;
    }
    return RuleHelper.isValidMethod(from, access, name);
  }
}
