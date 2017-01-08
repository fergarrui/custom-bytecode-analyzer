package net.nandgr.cba.custom.visitor.helper;

import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.model.Rule;
import net.nandgr.cba.custom.model.Rules;
import net.nandgr.cba.exception.BadRulesException;
import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Opcodes;

public class RuleHelper {

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
      isValid &=  access == ruleVisibility;
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

  private static int getOpcodeVisibility(String visibility) {
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
