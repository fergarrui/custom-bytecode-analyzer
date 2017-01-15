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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.nandgr.cba.custom.model.Field;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.model.Rule;
import net.nandgr.cba.custom.model.Rules;
import net.nandgr.cba.exception.BadRulesException;
import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleHelper {

  private static final Logger logger = LoggerFactory.getLogger(RuleHelper.class);

  private RuleHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }

  public static void validateRules(Rules rules) throws BadRulesException {
    for (Rule rule : rules.getRules()) {
      if (StringUtils.isBlank(rule.getName())) {
        throw new BadRulesException("Rule name cannot be blank.");
      }
      validateRule(rule);
    }
  }

  private static void validateRule(Rule rule) throws BadRulesException {
    if (rule.getInvocations() != null) {
      for (Invocation invocation : rule.getInvocations()) {
        Method notFrom = invocation.getNotFrom();
        if (allEmpty(notFrom)) {
          throw new BadRulesException("\"invocation.notFrom\" property cannot have all the fields blank.");
        }
        Method from = invocation.getFrom();
        if (allEmpty(from)) {
          throw new BadRulesException("\"invocation.from\" property cannot have all the fields blank.");
        }
        if (notFrom != null && from != null) {
          throw new BadRulesException("\"invocation.notFrom\" and \"invocation.from\" cannot be defined at the same time in the same rule.");
        }
        Method method = invocation.getMethod();
        if (allEmpty(method)) {
          throw new BadRulesException("\"invocation.method\" property cannot have all the fields blank.");
        }
      }
    }
  }

  private static boolean allEmpty(Method method) {
    if (method == null) {
      return false;
    }
    return StringUtils.isBlank(method.getName()) && StringUtils.isBlank(method.getParameter()) && StringUtils.isBlank(method.getVisibility());
  }

  public static boolean isValidMethod(Method method, int access, String name, String desc) {
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
    String parameter = method.getParameter();
    if (parameter != null) {
      isValid &= desc.contains(StringsHelper.dotsToSlashes(parameter));
    }
    return isValid;
  }

  public static boolean isValidMethodInvocation(Invocation invocation, String owner, String name, String desc) {
    boolean isValid = true;
    Method invocationMethod = invocation.getMethod();
    if (invocationMethod != null) {
      isValid &= isValidMethod(invocationMethod, 0, name, desc );
    }
    String invocationOwner = invocation.getOwner();
    if (invocationOwner != null) {
      isValid &= owner.equals(StringsHelper.dotsToSlashes(invocationOwner));
    }
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
}
