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

import java.util.Arrays;
import java.util.List;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.report.ReportItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomClassInterfacesVisitor extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomClassInterfacesVisitor.class);
  private final List<String> ruleInterfaces;

  public CustomClassInterfacesVisitor(List<String> ruleInterfaces, String ruleName) {
    super(ruleName);
    this.ruleInterfaces = ruleInterfaces;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    logger.trace("visit class: version={} access={} name={} signature={} superName={} interfaces={}", version, access, name, signature, superName, interfaces);
    if (validInterfaces(Arrays.asList(interfaces), ruleInterfaces)) {
      ReportItem reportItem = new ReportItem(-1, null, getRuleName());
      this.setIssueFound(true);
      this.itemsFound().add(reportItem);
    }
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public boolean showInReport() {
    return true;
  }

  private boolean validInterfaces(List<String> visitorInterfaces, List<String> ruleInterfaces) {
    boolean valid = true;
    for (String ruleInterface : ruleInterfaces) {
      valid &= visitorInterfaces.contains(StringsHelper.dotsToSlashes(ruleInterface));
    }
    return valid;
  }
}
