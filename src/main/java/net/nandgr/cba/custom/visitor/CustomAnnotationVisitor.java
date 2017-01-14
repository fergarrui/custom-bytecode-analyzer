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

import net.nandgr.cba.custom.model.Annotation;
import net.nandgr.cba.custom.visitor.helper.StringsHelper;
import net.nandgr.cba.report.ReportItem;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAnnotationVisitor extends CustomAbstractVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomAnnotationVisitor.class);

  private final Annotation annotation;

  public CustomAnnotationVisitor(Annotation annotation, String ruleName) {
    super(ruleName);
    this.annotation = annotation;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    logger.trace("visitAnnotation: desc={}, visible={}", desc, visible);
    if (StringsHelper.simpleDescriptorToHuman(desc).equals(StringsHelper.dotsToSlashes(annotation.getType()))) {
      ReportItem reportItem = new ReportItem(-1, null, getRuleName());
      this.setIssueFound(true);
      this.itemsFound().add(reportItem);
    }
    return super.visitAnnotation(desc, visible);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    logger.trace("visitMethod: access={} name={} desc={} signature={} exceptions={}", access, name, desc, signature, exceptions);
    CustomMethodAnnotationVisitor customMethodAnnotationVisitor = new CustomMethodAnnotationVisitor(this, annotation, name);
    return customMethodAnnotationVisitor;
  }

  @Override
  public boolean showInReport() {
    return annotation.isReport();
  }
}
