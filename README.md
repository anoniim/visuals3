Gradle Setup
===

I tried to avoid having to include the library JARs in the project as much as I could, but it was not sustainable.
I want this project to be runnable with minimum effort and that was no longer possible.
That is because there are some dependencies (including some specific to Processing) that cannot be obtained from maven.

Therefore, all project dependencies are included in the project (in `/libs` folder).

### Maven

For those curious, there are the instruction to use maven:

Using Kotlin DSL for Gradle, the Processing dependency can be defined as follows:
```gradle
implementation(group = "org.processing", name = "core", version = "3.3.7")
```

#### Public maven

The public maven artefacts (taken from `mavenCentral()`) are not owned by the Processing Foundation and therefore are out of date (only version up to 3.3.7 available). 

#### Local maven

For a newer version, download Processing, open the application files and install the library file in local maven repository.
On MacOS, this can be done by:

```shell
cd /Applications/Processing.app/Contents/Java/core/library
mvn install:install-file -Dfile=core.jar -DgroupId=org.processing -DartifactId=core -Dversion=3.5.4 -Dpackaging=jar
```
```gradle
implementation(group = "org.processing", name = "core", version = "3.5.4")
```
Same technique can be used to install any number of Processing libraries.
Just download the library via Processing PDE and install the JARs in local maven by the method described above.

## Processing 4

The project is using the latest version of Processing 4.0. 
The binaries are included in the project.

Java 11 is needed to run Processing 4 sketches. 
Everything else seems to work fine so far.

## Libraries

The project uses the following Processing libraries:

* [Processing Sound library](https://processing.org/reference/libraries/sound/index.html)
* [VideoExport by Abe Pazos](https://github.com/hamoid/video_export_processing)

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

`BaseSketch` TBD

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
Looks like a missing library dependency, even though the JavaFx JARs are now included in the project.