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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMethodAnnotationVisitor extends MethodVisitor {

  private static final Logger logger = LoggerFactory.getLogger(CustomMethodAnnotationVisitor.class);

  private final CustomVisitor parentVisitor;
  private final Annotation annotation;
  private int lineNumber;
  private final String methodName;

  public CustomMethodAnnotationVisitor(CustomVisitor parentVisitor, Annotation annotation, String methodName) {
    super(Opcodes.ASM4);
    this.parentVisitor = parentVisitor;
    this.annotation = annotation;
    this.methodName = methodName;
    this.lineNumber = -1;
  }

  @Override
  public void visitLineNumber(int line, Label start) {
    lineNumber = line;
    super.visitLineNumber(line, start);
  }

  @Override
  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
    logger.trace("visitParameterAnnotation: parameter={}, desc={}, visible={}",parameter, desc, visible);
    checkValidAnnotation(desc);
    return super.visitParameterAnnotation(parameter, desc, visible);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    logger.trace("visitAnnotation: desc={}, visible={}", desc, visible);
    checkValidAnnotation(desc);
    return super.visitAnnotation(desc, visible);
  }

  private void checkValidAnnotation(String desc) {
    if (StringsHelper.simpleDescriptorToHuman(desc).equals(StringsHelper.dotsToSlashes(annotation.getType()))) {
      ReportItem reportItem = new ReportItem(lineNumber, methodName, null, parentVisitor.getRuleName(), parentVisitor.showInReport());
      parentVisitor.setIssueFound(true);
      parentVisitor.itemsFound().add(reportItem);
    }
  }
}
