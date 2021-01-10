Gradle Setup
===

Using Kotlin DSL for Gradle, the Processing dependency can be defined as follows:
```gradle
implementation(group = "org.processing", name = "core", version = "3.5.4")
```

### Public maven

For out-of-the-box setup use the public maven artefacts (taken from `mavenCentral()`). 
Beware that these are not officially maintained and thus are outdated (only version up to 3.3.7 available). 

### Local maven

For a newer version, download Processing, open the application files and install the library file in local maven repository.
On MacOS, this can be done by:

```shell
cd /Applications/Processing.app/Contents/Java/core/library
mvn install:install-file -Dfile=core.jar -DgroupId=org.processing -DartifactId=core -Dversion=3.5.4 -Dpackaging=jar
```

Running Sketches with Kotlin
===

To run a Processing Sketch, you need to run the static `PApplet.main()` method. 
This method takes an array of PApplet class names to run.
To avoid having to add the main method to every sketch, type the applet class name and change the run configuration every time, there is the `SketchLibrary` class that contains all sketches with class references. 
The application then needs just a single Run Configuration that launches `SketchLibraryKt` main class.

```kotlin
fun main() {
    PApplet.main(arrayOf(Bubbles::class.qualifiedName))
}
```

Whenever I create a new sketch, I add it to this class and comment the old one out.

Convenience methods
===

`BaseSketch`

### Sketch Recording + create GIFs 

I added a convenience method to my `.bash_profile`.
It takes all `.png` files in given folder and creates a GIF of the same name. 

```shell
makegif() {
  name=$1
  framerate=30
  if [[ "$2" ]] then
    framerate=$2
  fi
  ffmpeg -f image2 -pattern_type glob -framerate $framerate -i ${name}'/*.png' $name.gif
}
```
Then I can go to a location where the folder with captured PNGs is present and run:
```shell
makegif bubbles
```

Known issues
=== 

### Only the default renderer works

`P2D` and `P3D` renderers fail with the following error on MacOS.

```text
2021-01-10 20:47:22.022 java[44072:686817] Apple AWT Internal Exception: NSWindow drag regions should only be invalidated on the Main Thread!
2021-01-10 20:47:22.022 java[44072:686817] *** Terminating app due to uncaught exception 'NSInternalInconsistencyException', reason: 'NSWindow drag regions should only be invalidated on the Main Thread!'
*** First throw call stack:
(
	0   CoreFoundation                      0x00007fff382dcb57 __exceptionPreprocess + 250
	1   libobjc.A.dylib                     0x00007fff711285bf objc_exception_throw + 48
	2   CoreFoundation                      0x00007fff3830534c -[NSException raise] + 9
	3   AppKit                              0x00007fff354ff5ec -[NSWindow(NSWindow_Theme) _postWindowNeedsToResetDragMarginsUnlessPostingDisabled] + 310
	4   AppKit                              0x00007fff354e7052 -[NSWindow _initContent:styleMask:backing:defer:contentView:] + 1416
	5   AppKit                              0x00007fff354e6ac3 -[NSWindow initWithContentRect:styleMask:backing:defer:] + 42
	6   libnativewindow_macosx.jnilib       0x000000012ddac3fe Java_jogamp_nativewindow_macosx_OSXUtil_CreateNSWindow0 + 398
	7   ???                                 0x000000010c957407 0x0 + 4506088455
)
libc++abi.dylib: terminating with uncaught exception of type NSException
```
This is a [known issue](https://github.com/processing/processing/issues/5983) and has apparently been fixed in Processing 4.0 but still happens to me when running the project in the current setup.

`FX2D` fails with: 
```text
Caused by: java.lang.ClassNotFoundException: javafx.scene.image.PixelFormat
```
Might be a missing library dependency, I only import `org.processing.core`.