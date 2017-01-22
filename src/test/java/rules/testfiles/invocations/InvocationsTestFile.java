package rules.testfiles.invocations;

public class InvocationsTestFile {
  class A {
    private void method() {
      String a = "";
      System.out.println(a.toString());
      Boolean b = true;
      System.out.println(b.toString());
      boolean c = a.equals(b);
    }
  }
  class B {
    public void method3() {
      String a = "";
      System.out.println(a.toString());
      Boolean b = true;
      System.out.println(b.toString());
      boolean c = a.equals(b);
    }
  }
  class C {
    public void method3() {
      String a = "";
      System.out.println(a);
      Boolean b = true;
      System.out.println(b.getClass());
      boolean c = a.equals(b);
    }
  }
  class D {
    public void method3() {
      String a = "";
      System.out.println(a.toString());
    }
  }
}
