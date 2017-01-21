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

import java.util.ArrayList;
import java.util.List;
import net.nandgr.cba.custom.model.Annotation;
import net.nandgr.cba.custom.visitor.base.CustomAbstractClassVisitor;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.report.ReportItem;
import org.objectweb.asm.tree.AnnotationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomClassAnnotationVisitor extends CustomAbstractClassVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomClassAnnotationVisitor.class);

  private final Annotation annotation;

  public CustomClassAnnotationVisitor(Annotation annotation, String ruleName) {
    super(ruleName);
    this.annotation = annotation;
  }

  @Override
  public void process() {
    boolean issueFound = true;
    List<AnnotationNode> allAnnotations = new ArrayList<>();
    List<AnnotationNode> visibleAnnotations = getClassNode().visibleAnnotations;
    if (visibleAnnotations != null) {
      allAnnotations.addAll(visibleAnnotations);
    }
    List<AnnotationNode> invisibleAnnotations = getClassNode().invisibleAnnotations;
    if (invisibleAnnotations != null) {
      allAnnotations.addAll(invisibleAnnotations);
    }
    for (AnnotationNode annotationNode : allAnnotations) {
      String desc = annotationNode.desc;
      boolean visible = visibleAnnotations == null ? false : visibleAnnotations.contains(annotationNode);
      logger.trace("visitAnnotation: desc={}, visible={}", desc, visible);
      issueFound = StringsHelper.simpleDescriptorToHuman(desc).equals(StringsHelper.dotsToSlashes(annotation.getType()));
      if (issueFound) {
        ReportItem reportItem = new ReportItem(-1, null, null,getRuleName(), showInReport());
        this.itemsFound().add(reportItem);
        this.setIssueFound(true);
      }
    }
  }

  @Override
  public boolean showInReport() {
    return annotation.isReport();
  }
}
