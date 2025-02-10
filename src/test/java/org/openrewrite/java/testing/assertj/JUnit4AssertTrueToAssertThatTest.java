/*
 * Copyright 2024 the original author or authors.
 * <p>
 * Licensed under the Moderne Source Available License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://docs.moderne.io/licensing/moderne-source-available-license
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.testing.assertj;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

import static org.openrewrite.java.Assertions.java;

@SuppressWarnings({"ConstantConditions", "ExcessiveLambdaUsage", "java:S2699"})
class JUnit4AssertTrueToAssertThatTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .parser(JavaParser.fromJavaVersion()
            .classpathFromResources(new InMemoryExecutionContext(), "junit-4.13.2"))
          .recipe(new JUnitAssertTrueToAssertThat());
    }


    @DocumentExample
    @Test
    void singleStaticMethodNoMessage() {
        //language=java
        rewriteRun(
          java(
            """
              import org.junit.Test;

              import static org.junit.Assert.assertTrue;

              public class MyTest {
                  @Test
                  public void test() {
                      assertTrue(notification() != null && notification() > 0);
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """,
            """
              import org.junit.Test;

              import static org.assertj.core.api.Assertions.assertThat;

              public class MyTest {
                  @Test
                  public void test() {
                      assertThat(notification() != null && notification() > 0).isTrue();
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """
          )
        );
    }

    @Test
    void singleStaticMethodWithMessageString() {
        //language=java
        rewriteRun(
          spec -> spec.typeValidationOptions(TypeValidation.none()),
          java(
            """
              import org.junit.Test;

              import static org.junit.jupiter.api.Assertions.*;

              public class MyTest {
                  @Test
                  public void test() {
                      assertTrue("The notification should be positive", notification() != null && notification() > 0);
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """,
            """
              import org.junit.Test;

              import static org.assertj.core.api.Assertions.assertThat;

              public class MyTest {
                  @Test
                  public void test() {
                      assertThat(notification() != null && notification() > 0).as("The notification should be positive").isTrue();
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """
          )
        );
    }

    @Test
    void inlineReference() {
        //language=java
        rewriteRun(
          spec -> spec.typeValidationOptions(TypeValidation.none()),
          java(
            """
              import org.junit.Test;

              public class MyTest {
                  @Test
                  public void test() {
                      org.junit.Assert.assertTrue(notification() != null && notification() > 0);
                      org.junit.Assert.assertTrue("The notification should be positive", notification() != null && notification() > 0);
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """,
            """
              import org.junit.Test;

              import static org.assertj.core.api.Assertions.assertThat;

              public class MyTest {
                  @Test
                  public void test() {
                      assertThat(notification() != null && notification() > 0).isTrue();
                      assertThat(notification() != null && notification() > 0).as("The notification should be positive").isTrue();
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """
          )
        );
    }

    @Test
    void mixedReferences() {
        //language=java
        rewriteRun(
          spec -> spec.typeValidationOptions(TypeValidation.none()),
          java(
            """
              import org.junit.Test;

              import static org.assertj.core.api.Assertions.*;
              import static org.junit.Assert.assertTrue;

              public class MyTest {
                  @Test
                  public void test() {
                      assertTrue(notification() != null && notification() > 0);
                      org.junit.Assert.assertTrue("The notification should be positive", notification() != null && notification() > 0);
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """,
            """
              import org.junit.Test;

              import static org.assertj.core.api.Assertions.*;

              public class MyTest {
                  @Test
                  public void test() {
                      assertThat(notification() != null && notification() > 0).isTrue();
                      assertThat(notification() != null && notification() > 0).as("The notification should be positive").isTrue();
                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """
          )
        );
    }

    @Test
    void leaveBooleanSuppliersAlone() {
        //language=java
        rewriteRun(
          spec -> spec.typeValidationOptions(TypeValidation.none()),
          java(
            """
              import org.junit.Test;

              import static org.junit.Assert.assertTrue;

              public class MyTest {
                  @Test
                  public void test() {
                      assertTrue(notification() != null && notification() > 0);
                      assertTrue("The notification should be positive", notification() != null && notification() > 0);

                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """,
            """
              import org.junit.Test;

              import static org.assertj.core.api.Assertions.assertThat;
              import static org.junit.Assert.assertTrue;

              public class MyTest {
                  @Test
                  public void test() {
                      assertThat(notification() != null && notification() > 0).isTrue();
                      assertThat(notification() != null && notification() > 0).as("The notification should be positive").isTrue();

                  }
                  private Integer notification() {
                      return 1;
                  }
              }
              """
          )
        );
    }

}
