# java-lite-cpreprocessor
Ultra lite C preprocessor. Using  java annotation processor
<br>
Tested jvm versions: 8

# Example

### JAVA SOURCE
```java
/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */

package annotationtest;

import net.steelswing.clp.annotation.Define;
import net.steelswing.clp.annotation.IfDefine;
import net.steelswing.clp.annotation.IfNotDefine;

/**
 *
 * @author LWJGL2
 */
@Define({
    "USE_PRINT",
    "NOT_PRINT_WELCOME_MESSAGE"
})
public class AnnotationTest {

    @IfDefine("USE_PRINT")
    public void print(String message) {
        System.out.println(message);
    }

    // throwsException - use default return value instead of exception
    @IfNotDefine(value = {"NOT_PRINT_WELCOME_MESSAGE"}, throwsException = false)
    public void printWelcome() {
        print("Welcome!");
    }

    public static void main(String[] args) {
        AnnotationTest t = new AnnotationTest();
        t.printWelcome();
        t.print("Hello World");
    }

}
```
### JAVA DECOMPILED CODE
```java
package annotationtest;

public class AnnotationTest {
    public AnnotationTest() {
        super();
    }
    
    public void print(final String message) {
        System.out.println(message);
    }
    
    public void printWelcome() {
        // empty method because Defined NOT_PRINT_WELCOME_MESSAGE
    }
    
    public static void main(final String[] args) {
        final AnnotationTest t = new AnnotationTest();
        t.printWelcome();
        t.print("Hello World");
    }
}
```

