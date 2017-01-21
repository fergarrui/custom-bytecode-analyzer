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

import net.nandgr.cba.custom.visitor.base.CustomVisitor;
import net.nandgr.cba.report.ReportItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class RuleVisitorsAnalyzer {

  private final List<CustomVisitor> visitorList = new ArrayList<>();

  public List<CustomVisitor> getVisitorList() {
    return visitorList;
  }

  public List<ReportItem> runRules(InputStream inputStream) throws IOException {
    boolean meetsAllVisitors = true;
    List<ReportItem> reportItems = new ArrayList<>();
    ClassReader classReader = new ClassReader(inputStream);
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode,0);

    for (CustomVisitor customVisitor : visitorList) {
      customVisitor.setNode(classNode);
      customVisitor.process();
      meetsAllVisitors &= customVisitor.issueFound();
      if (!meetsAllVisitors) {
        return new ArrayList<>();
      }
      for (ReportItem reportItem : customVisitor.itemsFound()) {
        if (reportItem.isShowInReport()) {
          reportItems.add(reportItem);
        }
      }
      customVisitor.clear();
    }
    return reportItems;
  }
}
