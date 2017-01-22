package rules.testfiles.annotations;

import com.google.gson.annotations.Since;

public class AnnotationsTestFile {
  @Deprecated
  @Since(1)
  class A {
  }
  class B {
  }
  class C {
    @Deprecated
    void method1() {
    }
  }
  @Since(1)
  class D {
    @Deprecated()
    void method2(@Deprecated Integer a) {
    }
  }
  @Deprecated
  class E {

  }
  class F {

  }
  @Deprecated
  class G {
    @Deprecated
    void method(@Deprecated Integer a) {
    }
  }
}
