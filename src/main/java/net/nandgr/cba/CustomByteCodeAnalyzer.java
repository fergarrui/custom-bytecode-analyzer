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
package net.nandgr.cba;

import java.lang.reflect.InvocationTargetException;
import net.nandgr.cba.custom.model.Annotation;
import net.nandgr.cba.custom.model.Field;
import net.nandgr.cba.custom.model.Rule;
import net.nandgr.cba.custom.model.Rules;
import net.nandgr.cba.custom.visitor.CustomClassAnnotationVisitor;
import net.nandgr.cba.custom.visitor.CustomClassInterfacesVisitor;
import net.nandgr.cba.custom.visitor.CustomClassSuperClassVisitor;
import net.nandgr.cba.custom.visitor.CustomFieldVisitor;
import net.nandgr.cba.custom.visitor.CustomMethodInvocationVisitor;
import net.nandgr.cba.custom.visitor.base.CustomVisitor;
import net.nandgr.cba.report.ReportItem;
import net.nandgr.cba.cli.CliHelper;
import net.nandgr.cba.custom.model.Invocation;
import net.nandgr.cba.custom.model.Method;
import net.nandgr.cba.custom.visitor.CustomMethodVisitor;
import net.nandgr.cba.custom.visitor.RuleVisitorsAnalyzer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomByteCodeAnalyzer implements ByteCodeAnalyzer {

  private static final Logger logger = LoggerFactory.getLogger(CustomByteCodeAnalyzer.class);

  private static final String CHECKS_PACKAGE = "net.nandgr.cba.visitor.checks.";
  private final List<RuleVisitorsAnalyzer> ruleVisitorsAnalyzers = new ArrayList<>();

  public CustomByteCodeAnalyzer(boolean hasCustomFile, boolean hasChecks, Rules rules) throws ReflectiveOperationException, IOException {
    if (hasCustomFile) {
      populateCustomVisitors(rules);
    }
    if (hasChecks) {
      populateStaticVisitors();
    }
  }

  private void populateCustomVisitors(Rules rules) throws IOException {
    logger.debug("Processing {} rule(s)", rules.getRules().size());
    for (Rule rule : rules.getRules()) {
      logger.debug("Rule {} added.", rule.getName());
      RuleVisitorsAnalyzer ruleVisitorsAnalyzer = new RuleVisitorsAnalyzer();

      List<Method> methods = rule.getMethods();
      addMethods(ruleVisitorsAnalyzer, methods, rule);

      List<Invocation> invocations = rule.getInvocations();
      addInvocations(ruleVisitorsAnalyzer, invocations, rule);

      List<String> interfaces = rule.getInterfaces();
      addInterfaces(ruleVisitorsAnalyzer, interfaces, rule);

      String superClass = rule.getSuperClass();
      addSuperClass(ruleVisitorsAnalyzer, superClass, rule);

      List<Annotation> annotations = rule.getAnnotations();
      addAnnotations(ruleVisitorsAnalyzer, annotations, rule);

      List<Field> fields = rule.getFields();
      addFields(ruleVisitorsAnalyzer, fields, rule);

      this.ruleVisitorsAnalyzers.add(ruleVisitorsAnalyzer);
    }
    logger.debug("Rules processed.");
  }

  private static void addFields(RuleVisitorsAnalyzer ruleVisitorsAnalyzer, List<Field> fields, Rule rule) {
    if (fields != null && !fields.isEmpty()) {
      for (Field field : fields) {
        CustomFieldVisitor customFieldVisitor = new CustomFieldVisitor(field, rule.getName());
        ruleVisitorsAnalyzer.getVisitorList().add(customFieldVisitor);
      }
    }
  }

  private void addAnnotations(RuleVisitorsAnalyzer ruleVisitorsAnalyzer, List<Annotation> annotations, Rule rule) {
    if (annotations != null && !annotations.isEmpty()) {
      for (Annotation annotation : annotations) {
        CustomClassAnnotationVisitor customClassAnnotationVisitor = new CustomClassAnnotationVisitor(annotation, rule.getName());
        ruleVisitorsAnalyzer.getVisitorList().add(customClassAnnotationVisitor);
      }
    }
  }

  private static void addSuperClass(RuleVisitorsAnalyzer ruleVisitorsAnalyzer, String superClass, Rule rule) {
    if (!StringUtils.isBlank(superClass)) {
      CustomClassSuperClassVisitor customClassSuperClassVisitor = new CustomClassSuperClassVisitor(superClass, rule.getName());
      ruleVisitorsAnalyzer.getVisitorList().add(customClassSuperClassVisitor);
    }
  }

  private static void addInterfaces(RuleVisitorsAnalyzer ruleVisitorsAnalyzer, List<String> interfaces, Rule rule) {
    if (interfaces != null && !interfaces.isEmpty()) {
      CustomClassInterfacesVisitor customClassInterfacesVisitor = new CustomClassInterfacesVisitor(interfaces, rule.getName());
      ruleVisitorsAnalyzer.getVisitorList().add(customClassInterfacesVisitor);
    }
  }

  private static void addInvocations(RuleVisitorsAnalyzer ruleVisitorsAnalyzer, List<Invocation> invocations, Rule rule) {
    if (invocations != null) {
      for (Invocation invocation : invocations) {
        CustomMethodInvocationVisitor customMethodInvocationVisitor = new CustomMethodInvocationVisitor(invocation, rule.getName());
        ruleVisitorsAnalyzer.getVisitorList().add(customMethodInvocationVisitor);
      }
    }
  }

  private static void addMethods(RuleVisitorsAnalyzer ruleVisitorsAnalyzer, List<Method> methods, Rule rule) {
    if (methods != null) {
      for (Method method : methods) {
        CustomMethodVisitor customMethodVisitor = new CustomMethodVisitor(method, rule.getName());
        ruleVisitorsAnalyzer.getVisitorList().add(customMethodVisitor);
      }
    }
  }

  private void populateStaticVisitors() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    String[] checks = CliHelper.getChecks();
    if (checks == null) {
      return;
    }
    List<Class<? extends CustomVisitor>> classVisitorList = new ArrayList<>();
    for (String check : checks) {
      Class checkClass = App.class.forName(CHECKS_PACKAGE + check);
      classVisitorList.add(checkClass);
    }
      for (Class<? extends CustomVisitor> classVisitor : classVisitorList) {
        Constructor constructor = classVisitor.getDeclaredConstructor();
        CustomVisitor reporterClassVisitor = (CustomVisitor) constructor.newInstance();
        RuleVisitorsAnalyzer ruleVisitorsAnalyzer = new RuleVisitorsAnalyzer();
        ruleVisitorsAnalyzer.getVisitorList().add(reporterClassVisitor);
        this.ruleVisitorsAnalyzers.add(ruleVisitorsAnalyzer);
      }
  }

  @Override
  public List<ReportItem> analyze(InputStream inputStream) {
    logger.debug("Analyzing file... ");
    final List<ReportItem> reportItems = new ArrayList<>();
    try {
      // bytes backup to create a ByteArrayInputStream at every analysis, since ASM ClassReader closes the InputStream
      byte[] inputStreamBytes = IOUtils.toByteArray(inputStream);
      for (RuleVisitorsAnalyzer ruleVisitorsAnalyzer : ruleVisitorsAnalyzers) {
        InputStream byteArrayInputStream = new ByteArrayInputStream(inputStreamBytes);
        reportItems.addAll(ruleVisitorsAnalyzer.runRules(byteArrayInputStream));
      }
    } catch (Exception e) {
      logger.error("Error while analyzing inputStream", e);
    }
    logger.debug("File analyzed, {} issue(s) found.", reportItems.size());
    return reportItems;
  }
}
