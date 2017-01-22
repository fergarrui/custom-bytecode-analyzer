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

import net.nandgr.cba.custom.model.Field;
import net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor;
import net.nandgr.cba.custom.visitor.helper.RuleHelper;
import net.nandgr.cba.report.ReportItem;
import org.objectweb.asm.tree.FieldNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFieldVisitor extends CustomAbstractClassVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomFieldVisitor.class);
  private final Field field;

  public CustomFieldVisitor(Field field, String ruleName) {
    super(ruleName);
    this.field = field;
  }

  @Override
  public void process() {
    boolean issueFound;
    for (FieldNode fieldNode : getClassNode().fields) {
      int access = fieldNode.access;
      String name = fieldNode.name;
      String desc = fieldNode.desc;
      String signature = fieldNode.signature;
      Object value = fieldNode.value;
      logger.trace("visitField: access={}, name={}, desc={}, signature={}, value={}", access, name, desc, signature, value);
      issueFound = RuleHelper.isValidField(field, access, name, desc, signature, value);
      if (issueFound) {
        ReportItem reportItem = new ReportItem(getRuleName(), showInReport()).addProperty("Field name", name);
        itemsFound().add(reportItem);
        setIssueFound(issueFound);
      }
    }
  }

  @Override
  public boolean showInReport() {
    return field.isReport();
  }
}
