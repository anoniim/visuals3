Gradle Setup
===

Using Kotlin DSL for Gradle, the Processing dependency can be defined as follows:
```gradle
implementation(group = "org.processing", name = "core", version = "3.3.7")
```

## Public maven

For out-of-the-box setup use the public maven artefacts (taken from `mavenCentral()`). 
Beware that these are not officially maintained and thus are outdated (only version up to 3.3.7 available). 

## Local maven

For a newer version, download Processing, open the application files and install the library file in local maven repository.
On MacOS, this can be done by:

```shell
cd /Applications/Processing.app/Contents/Java/core/library
mvn install:install-file -Dfile=core.jar -DgroupId=org.processing -DartifactId=core -Dversion=3.5.4 -Dpackaging=jar
```
```gradle
implementation(group = "org.processing", name = "core", version = "3.3.7")
```

### Processing 4

Using this technique, we can also get the latest Processing 4.0

```gradle
implementation(group = "org.processing", name = "core", version = "4.0a2")
```

The only thing this needs is Java 11. Everything else seems to work fine so far.

## Sound

To use [Processing Sound library](https://processing.org/reference/libraries/sound/index.html), download the library via Processing PDE and install the the 2 JARs in local maven by the method described above.

```shell
mvn install:install-file -Dfile=sound.jar -DgroupId=org.processing -DartifactId=sound -Dversion=2.2.3 -Dpackaging=jar
mvn install:install-file -Dfile=javamp3-1.0.4.jar -DgroupId=org.processing -DartifactId=sound -Dversion=2.2.3 -Dpackaging=jar -Dclassifier=javamp3
mvn install:install-file -Dfile=jsyn-20171016.jar -DgroupId=org.processing -DartifactId=sound -Dversion=2.2.3 -Dpackaging=jar -Dclassifier=jsyn
``` 

**TODO:** Work out how to add `javamp3` and `jsyn` as dependencies of the main (`sound`) artefact 

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

### FX2D renderer doesn't work

When run with `FX2D` renderer, the sketch fails to load with: 
```text
Caused by: java.lang.ClassNotFoundException: javafx.scene.image.PixelFormat
```
Looks like a missing library dependency, even though there is now JavaFx included in the project as a JAR.