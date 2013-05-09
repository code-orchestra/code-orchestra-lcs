-injars /Users/buildserver/git/flex-sdk-livecoding/lib/mxmlc.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/asc.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/asdoc.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/compc.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/digest.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/fcsh.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/fdb.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/flex-compiler-oem.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/flexTasks.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/fxgutils.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/mxmlc_ja.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/optimizer.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/swcdepends.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/swfdump.jar
-injars /Users/buildserver/git/flex-sdk-livecoding/lib/swfutils.jar
-outjars /Users/buildserver/TMP/COLT/COLT/flex_sdk/liblc

-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/batik-all-flex.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/commons-lang3-3.1.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/copylocale.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/velocity-dep-1.4-flex.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/yjp-controller-api-redist.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/ant.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/commons-collections.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/commons-discovery.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/commons-lang3-3.1.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/commons-logging.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/jacl-1.2.6.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/javacc.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/jython-2.2-beta1.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/saxon9.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/tcljava-1.2.6.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/xalan.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/xercesImpl.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/xercesPatch.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/xml-apis-ext.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/xml-apis.jar
-libraryjars /Users/buildserver/git/flex-sdk-livecoding/lib/external/yjp-controller-api-redist.jar
-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar

-target 1.6
-dontshrink
-dontoptimize
-keeppackagenames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
-keepparameternames
-microedition

-keep class !codeOrchestra.** { *; }

-keep,allowshrinking public final @interface  *

# Keep - Applications. Keep all application classes, along with their 'main'
# methods.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver

# Also keep - Swing UI L&F. Keep all extensions of javax.swing.plaf.ComponentUI,
# along with the special 'createUI' method.
-keep class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
