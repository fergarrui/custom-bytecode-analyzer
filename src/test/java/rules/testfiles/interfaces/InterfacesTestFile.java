package rules.testfiles.interfaces;

public class InterfacesTestFile {

  class A implements Comparable {
    @Override
    public int compareTo(Object o) {
      return 0;
    }
  }
  interface B {
  }
  class C implements B {
  }
  class D implements Comparable, B {
    @Override
    public int compareTo(Object o) {
      return 0;
    }
  }
  class E implements Comparable, F {
    @Override
    public int compareTo(Object o) {
      return 0;
    }
  }
  interface F {
  }
}
