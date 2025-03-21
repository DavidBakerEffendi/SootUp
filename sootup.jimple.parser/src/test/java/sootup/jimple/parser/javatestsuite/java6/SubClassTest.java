package sootup.jimple.parser.javatestsuite.java6;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.parser.categories.Java8Test;
import sootup.jimple.parser.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Category(Java8Test.class)
public class SubClassTest extends JimpleTestSuiteBase {
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "subclassMethod", "void", Collections.emptyList());
  }

  /** @returns the method signature needed for second method in testCase */
  public MethodSignature getMethodSignature1() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "superclassMethod", "void", Collections.emptyList());
  }

  @Test
  public void testSuperClassStmts() {
    SootMethod m = loadMethod(getMethodSignature1());
    assertJimpleStmts(m, expectedBodyStmts1());
    SootClass sootClass = loadClass(getDeclaredClassSignature());
    assertTrue(sootClass.getSuperclass().get().getClassName().equals("SuperClass"));
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: SubClass",
            "l0.<SubClass: int aa> = 10",
            "l0.<SubClass: int bb> = 20",
            "l0.<SubClass: int cc> = 30",
            "l0.<SubClass: int dd> = 40",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<String> expectedBodyStmts1() {
    return Stream.of(
            "l0 := @this: SubClass",
            "specialinvoke l0.<SuperClass: void superclassMethod()>()",
            "l0.<SubClass: int a> = 100",
            "l0.<SubClass: int b> = 200",
            "l0.<SubClass: int c> = 300",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
