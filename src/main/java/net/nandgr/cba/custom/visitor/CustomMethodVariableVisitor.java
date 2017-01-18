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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.nandgr.cba.custom.model.Variable;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.report.ReportItem;
import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodVariableVisitor extends MethodVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodVariableVisitor.class);

  private final CustomVisitor parent;
  private final Variable variable;
  private int lineNumber;
  private final String methodName;

  public CustomMethodVariableVisitor(CustomVisitor parent, Variable variable, String methodName) {
    super(Opcodes.ASM4);
    this.parent = parent;
    this.variable = variable;
    this.methodName = methodName;
  }

  @Override
  public void visitLineNumber(int line, Label start) {
    lineNumber = line;
    super.visitLineNumber(line, start);
  }

  @Override
  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    logger.trace("VisitLocalVariable: name={}, desc={}, signature={}, start={}, end={}, index={}", name, desc, signature, start, end, index);
    if (!name.contains("this") && isValidVariable(name, desc)) {
      ReportItem reportItem = new ReportItem(-1, methodName, name, parent.getRuleName(), parent.showInReport());
      parent.setIssueFound(true);
      parent.itemsFound().add(reportItem);
    }
    super.visitLocalVariable(name, desc, signature, start, end, index);
  }

  private boolean isValidVariable(String name, String desc) {
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
}
