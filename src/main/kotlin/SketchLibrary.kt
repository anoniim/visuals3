import processing.core.PApplet
import session.winter21.RevealingStripes
import sketch.dots.AtomicFlicker
import sketch.dots.CirclePendulum2
import sketch.dots.Pointillism
import sketch.growth.GrowingNeuron2
import sketch.lines.EasingLines
import sketch.perspective.RainbowSunrise
import sketch.perspective.TerrainGeneration
import sketch.pixels.BrightnessReveal
import sketch.sound.miso.EveningExperiment1Visualizer
import sketch.sound.zdenda.GrainVisualizer
import sketch.squares.LowResFire
import sketch.synchronization.SyncOrbits
import sketch.waves.BentSine
import sketch.waves.SineShell
import sketch.waves.TileWave

//import sketch.walkers.CityExploration

@ExperimentalStdlibApi
fun main() {


/* ______________ IN PROGRESS ______________ */
//    PApplet.main(MoireHexagons::class.java)
//    PApplet.main(BeatDetectorClock::class.java)
//    PApplet.main(GrowingTreeBranch::class.java)
//    PApplet.main(NeuronNetwork::class.java)
//    PApplet.main(GameOfLife::class.java)
//    PApplet.main(CubeLikePattern::class.java)
//    PApplet.main(CityExploration::class.java)
//    PApplet.main(AntiCollisionSystem::class.java)
//    PApplet.main(BrokenPianoStrings::class.java)
//    PApplet.main(FieldWalking::class.java)
//    PApplet.main(SinglePathMazeBuilder::class.java)
//    PApplet.main(AlgorithmicMazeBuilder::class.java)
//    PApplet.main(WorleyOutline::class.java)
//    PApplet.main(GrassFromAbove::class.java)
//    PApplet.main(TriangleDance::class.java)
//    PApplet.main(CircleDance::class.java)
//    PApplet.main(SineDance::class.java)
//    PApplet.main(SimpleSyncOrbit::class.java)
//    PApplet.main(Eyeballs::class.java)
//    PApplet.main(Clouds::class.java)
//    PApplet.main(SomeRosePatterns::class.java)
//    PApplet.main(Mandala::class.java)
//    PApplet.main(ToiletPaper::class.java)
//    PApplet.main(TriangularBackground::class.java)


/* ______________ PRACTICE ______________ */
//    PApplet.main(Broadcast::class.java)
//    PApplet.main(CircleLining::class.java)
//    PApplet.main(SkippingLines::class.java)
//    PApplet.main(CirclePacking::class.java)
//    PApplet.main(Bubbles::class.java)
//    PApplet.main(Starfield::class.java)
//    PApplet.main(StripyBlobLoop::class.java)
//    PApplet.main(WaterRipples::class.java)
//    PApplet.main(FireEffect::class.java)
//    PApplet.main(CollatzConjecture::class.java)
//    PApplet.main(TimesTablesCircle::class.java)
//    PApplet.main(MoireLines::class.java)
//    PApplet.main(BarVisualizer::class.java)
//    PApplet.main(QuadTreeTest::class.java)
//    PApplet.main(GrowingNeuron::class.java)
//    PApplet.main(AudioClock::class.java)
//    PApplet.main(CirclePendulum::class.java)
//    PApplet.main(Circadian::class.java)
//    PApplet.main(FoldingStripes::class.java)
//    PApplet.main(VoronoiTessellation::class.java)
//    PApplet.main(WorleyNoiseCells::class.java)
//    PApplet.main(HilbertCurve::class.java)
//    PApplet.main(MaurerRosePatterns2::class.java)
//    PApplet.main(TerrainGeneration::class.java)
//    PApplet.main(BrightnessReveal::class.java)
//    PApplet.main(SineShell::class.java)

/* ______________ TOOLS ______________ */
//    PApplet.main(Dot::class.java)
//    PApplet.main(TestWithVioleta::class.java)
//    PApplet.main(RosePatterns::class.java)
//    PApplet.main(EveningExperiment1Visualizer::class.java)
    PApplet.main(GrainVisualizer::class.java)
//    PApplet.main(FourierTransform::class.java)
//    PApplet.main(CuriosityBoxSummer21::class.java)
//    PApplet.main(EasingTest::class.java)


/* ______________ IDEAS ______________ */
//    PApplet.main(HyperbolicPlane::class.java)
//    PApplet.main(FocusedSquares::class.java)
//    PApplet.main(SquareSpiral::class.java)
//    PApplet.main(SpinningSpiral::class.java)
//    PApplet.main(HalfFill::class.java)
//    PApplet.main(EveningExperiment1Idea1::class.java)
//    PApplet.main(SineFlower::class.java)
//    PApplet.main(CurlingCircles::class.java)
//    PApplet.main(Prsoid::class.java)
//    PApplet.main(Flower1::class.java)
//    PApplet.main(SpaceSimulation::class.java)
//    PApplet.main(VortexGroups::class.java)
//    PApplet.main(CircleTangents::class.java)
//    PApplet.main(CirclePendulum3::class.java)
//    PApplet.main(SineWaveSync::class.java)
//    PApplet.main(WavyBackground::class.java)
//    PApplet.main(WavyBackgroundDrops::class.java)
//    PApplet.main(SpinningBean::class.java)
//    PApplet.main(CircleOscillator::class.java)
//    PApplet.main(FallApartImageTransformation::class.java)
//    PApplet.main(PolygonSpiralTunnel::class.java)
//    PApplet.main(PolygonStripeTunnel::class.java)
//    PApplet.main(PolygonSpiralFlower::class.java)
//    PApplet.main(InflatingCircles::class.java)
//    PApplet.main(Explorer::class.java)
//    PApplet.main(ExplorerMazeBuilder::class.java)
//    PApplet.main(NsormaFlameStar::class.java)
//    PApplet.main(SilhouetteSparks::class.java)
//    PApplet.main(Incircles::class.java)
//    PApplet.main(VipassanaWindow::class.java)
//    PApplet.main(LowResFire::class.java)


/* ______________ GOOD STUFF ______________ */
//    PApplet.main(PixelatedWalkers::class.java)
//    PApplet.main(SmoothWalkers::class.java)
//    PApplet.main(RaisingTiles::class.java)
//    PApplet.main(MatrixLetters::class.java)
//    PApplet.main(Toothpicks::class.java)
//    PApplet.main(NoisyWave::class.java)
//    PApplet.main(NoisyWaveSurface::class.java)
//    PApplet.main(ConstraintNoise::class.java)
//    PApplet.main(ConstrainedNoiseWithStencil::class.java)
//    PApplet.main(RainbowSunrise::class.java)
//    PApplet.main(CirclePendulum2::class.java)
//    PApplet.main(SyncOrbits::class.java)
//    PApplet.main(EasingLines::class.java)


/* ______________ SHOWCASE ______________ */
//    PApplet.main(SoupParticles::class.java)
//    PApplet.main(TextFromRandom::class.java)
//    PApplet.main(RecamansSequence::class.java)
//    PApplet.main(ZenSpiral::class.java)
//    PApplet.main(AnimatedRosePatterns::class.java)
//    PApplet.main(MaurerRosePatterns::class.java)
//    PApplet.main(CircleTangents2::class.java)
//    PApplet.main(EveningExperiment1Idea2::class.java)
//    PApplet.main(GrowingNeuron2::class.java)
//    PApplet.main(AtomicFlicker::class.java)
//    PApplet.main(TileWave::class.java)
//    PApplet.main(BentSine::class.java)
//    PApplet.main(RevealingStripes::class.java)
//    PApplet.main(Pointillism::class.java)
//    PApplet.main(DotOrnaments::class.java)
}
