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

import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomInvocationFinderVisitor extends MethodVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomInvocationFinderVisitor.class);

  private final CustomVisitor parentVisitor;
  private final Invocation methodInvocation;
  private int lineNumber;

  public CustomInvocationFinderVisitor(CustomVisitor parentVisitor, Invocation methodInvocation) {
    super(Opcodes.ASM4);
    this.parentVisitor = parentVisitor;
    this.methodInvocation = methodInvocation;
    this.lineNumber = -1;
  }

  @Override
  public void visitLineNumber(int line, Label start) {
    lineNumber = line;
    super.visitLineNumber(line, start);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    logger.trace("visitMethodInsn: opcode={} owner={} name={} desc={} itf={}", opcode, owner, name, desc, itf);
    if (RuleHelper.isValidMethodInvocation(methodInvocation, owner, name, desc)) {
      ReportItem reportItem = new ReportItem(lineNumber, name, null, parentVisitor.getRuleName(), parentVisitor.showInReport());
      parentVisitor.itemsFound().add(reportItem);
      logger.debug("Match found at method invocation - opcode: {}, owner: {}, name: {}, desc: {}, itf: {}", opcode,owner, name, desc);
      parentVisitor.setIssueFound(true);
    }
    super.visitMethodInsn(opcode, owner, name, desc, itf);
  }
}
