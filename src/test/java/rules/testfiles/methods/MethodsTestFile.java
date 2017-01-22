package rules.testfiles.methods;

public class MethodsTestFile {

  class A {
    public void method(String a, Integer b) {
    }
  }
  class B {
    private void method(String a, Integer b) {
    }
  }
  class C {
    private void method() {
      String a = new String();
      Integer b = new Integer(1);
      String c = "c";
    }
  }
  class D {
    public void method() {
      String a = new String();
      Integer b = new Integer(1);
      String c = "c";
      String passwd = "123456";
    }
  }
  class E {
    public void method(String param) {
      String a = new String();
      Integer b = new Integer(1);
      String c = "c";
      String passwd = "123456";
    }
  }
  class F {
    private void method() {
      String passwd = "123456";
    }
  }
}
