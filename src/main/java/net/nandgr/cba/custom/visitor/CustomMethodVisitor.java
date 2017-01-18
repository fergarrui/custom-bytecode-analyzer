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

import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodVisitor extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodVisitor.class);
  private final Method method;

  public CustomMethodVisitor(Method method, String ruleName) {
    super(ruleName);
    this.method = method;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    if (RuleHelper.isValidMethod(method, access, name, desc)) {
      ReportItem reportItem = new ReportItem(-1, name, null, getRuleName(), showInReport());
      this.itemsFound.add(reportItem);
      logger.debug("Issue found at method - access: {}, name: {}, desc: {}, signature: {}, exceptions: {}", access, name, desc, signature, exceptions);
      this.foundIssue = true;
    }
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  @Override
  public boolean showInReport() {
    return method.isReport();
  }
}
