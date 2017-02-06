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

import net.nandgr.cba.custom.visitor.base.CustomAbstractMethodInsnVisitor;
import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomInvocationFinderInsnVisitor extends CustomAbstractMethodInsnVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomInvocationFinderInsnVisitor.class);
  private final Invocation methodInvocation;
  private final String calledFrom;

  public CustomInvocationFinderInsnVisitor(String calledFrom, Invocation methodInvocation, String ruleName) {
    super(ruleName);
    this.calledFrom = calledFrom;
    this.methodInvocation = methodInvocation;
  }

  @Override
  public void process() {
    String name = getMethodInsnNode().name;
    String owner = getMethodInsnNode().owner;
    String desc = getMethodInsnNode().desc;
    logger.trace("visitMethodInsn: owner={} name={} desc={}", owner, name, desc);
    if (RuleHelper.isValidMethodInvocation(methodInvocation, owner, name, desc)) {
      ReportItem reportItem = new ReportItem(getRuleName(), showInReport())
              .addProperty("Method Name", calledFrom)
              .addProperty("Found method name", StringEscapeUtils.escapeHtml(name));

      itemsFound().add(reportItem);
      setIssueFound(true);
    }
  }

  @Override
  public boolean showInReport() {
    return methodInvocation.isReport();
  }
}
