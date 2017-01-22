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

import java.util.List;
import net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.report.ReportItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomClassSuperClassVisitor extends CustomAbstractClassVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomClassSuperClassVisitor.class);
  private final String superClass;

  public CustomClassSuperClassVisitor(String superClass, String ruleName) {
    super(ruleName);
    this.superClass = superClass;
  }

  @Override
  public void process() {
    int version = getClassNode().version;
    int access = getClassNode().access;
    String name = getClassNode().name;
    String signature = getClassNode().signature;
    String superName = getClassNode().superName;
    List<String> interfaces = getClassNode().interfaces;
    logger.trace("visit class: version={} access={} name={} signature={} superName={} interfaces={}", version, access, name, signature, superName, interfaces);

    if (superName.equals(StringsHelper.dotsToSlashes(superClass))) {
      ReportItem reportItem = new ReportItem(getRuleName(), showInReport());
      this.setIssueFound(true);
      this.itemsFound().add(reportItem);
    }
  }

  @Override
  public boolean showInReport() {
    return true;
  }
}
