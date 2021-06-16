import processing.core.PApplet
import sketch.mazes.AlgorithmicMazeBuilder
import sketch.mazes.ExplorerMazeBuilder
import sketch.patterns.VoronoiTessellation

@ExperimentalStdlibApi
fun main() {


/* ______________ IN PROGRESS ______________ */
//    PApplet.main(arrayOf(RoundingLines::class.qualifiedName))
//    PApplet.main(arrayOf(MoireHexagons::class.qualifiedName))
//    PApplet.main(arrayOf(BeatDetectorClock::class.qualifiedName))
//    PApplet.main(arrayOf(GrowingTreeBranch::class.qualifiedName))
//    PApplet.main(arrayOf(NeuronNetwork::class.qualifiedName))
//    PApplet.main(arrayOf(CircleOscillators::class.qualifiedName))
//    PApplet.main(arrayOf(GameOfLife::class.qualifiedName))
//    PApplet.main(arrayOf(CubeLikePattern::class.qualifiedName))
//    PApplet.main(arrayOf(CityExploration::class.qualifiedName))
//    PApplet.main(arrayOf(AntiCollisionSystem::class.qualifiedName))
//    PApplet.main(arrayOf(BrokenPianoStrings::class.qualifiedName))
//    PApplet.main(arrayOf(FieldWalking::class.qualifiedName))
//    PApplet.main(arrayOf(SinglePathMazeBuilder::class.qualifiedName))
//    PApplet.main(arrayOf(AlgorithmicMazeBuilder::class.qualifiedName))


/* ______________ PRACTICE ______________ */
//    PApplet.main(arrayOf(CircleLining::class.qualifiedName))
//    PApplet.main(arrayOf(SkippingLines::class.qualifiedName))
//    PApplet.main(arrayOf(CirclePacking::class.qualifiedName))
//    PApplet.main(arrayOf(Bubbles::class.qualifiedName))
//    PApplet.main(arrayOf(Starfield::class.qualifiedName))
//    PApplet.main(arrayOf(StripyBlobLoop::class.qualifiedName))
//    PApplet.main(arrayOf(WaterRipples::class.qualifiedName))
//    PApplet.main(arrayOf(FireEffect::class.qualifiedName))
//    PApplet.main(arrayOf(CollatzConjecture::class.qualifiedName))
//    PApplet.main(arrayOf(TimesTablesCircle::class.qualifiedName))
//    PApplet.main(arrayOf(MoireLines::class.qualifiedName))
//    PApplet.main(arrayOf(BarVisualizer::class.qualifiedName))
//    PApplet.main(arrayOf(QuadTreeTest::class.qualifiedName))
//    PApplet.main(arrayOf(GrowingNeuron::class.qualifiedName))
//    PApplet.main(arrayOf(AudioClock::class.qualifiedName))
//    PApplet.main(arrayOf(CirclePendulum::class.qualifiedName))
//    PApplet.main(arrayOf(Circadian::class.qualifiedName))
//    PApplet.main(arrayOf(FoldingStripes::class.qualifiedName))
    PApplet.main(arrayOf(VoronoiTessellation::class.qualifiedName))


/* ______________ TOOLS ______________ */
//    PApplet.main(arrayOf(Dot::class.qualifiedName))
//    PApplet.main(arrayOf(RosePatterns::class.qualifiedName))
//    PApplet.main(arrayOf(EveningExperiment1Visualizer::class.qualifiedName))
//    PApplet.main(arrayOf(FourierTransform::class.qualifiedName))


/* ______________ IDEAS ______________ */
//    PApplet.main(arrayOf(HyperbolicPlane::class.qualifiedName))
//    PApplet.main(arrayOf(FocusedSquares::class.qualifiedName))
//    PApplet.main(arrayOf(SquareSpiral::class.qualifiedName))
//    PApplet.main(arrayOf(SpinningSpiral::class.qualifiedName))
//    PApplet.main(arrayOf(HalfFill::class.qualifiedName))
//    PApplet.main(arrayOf(EveningExperiment1Idea1::class.qualifiedName))
//    PApplet.main(arrayOf(SineFlower::class.qualifiedName))
//    PApplet.main(arrayOf(CurlingCircles::class.qualifiedName))
//    PApplet.main(arrayOf(Prsoid::class.qualifiedName))
//    PApplet.main(arrayOf(SpaceSimulation::class.qualifiedName))
//    PApplet.main(arrayOf(VortexGroups::class.qualifiedName))
//    PApplet.main(arrayOf(CirclePendulum3::class.qualifiedName))
//    PApplet.main(arrayOf(SineWaveSync::class.qualifiedName))
//    PApplet.main(arrayOf(WavyBackground::class.qualifiedName))
//    PApplet.main(arrayOf(WavyBackgroundDrops::class.qualifiedName))
//    PApplet.main(arrayOf(SpinningBean::class.qualifiedName))
//    PApplet.main(arrayOf(CircleOscillator::class.qualifiedName))
//    PApplet.main(arrayOf(FallApartImageTransformation::class.qualifiedName))
//    PApplet.main(arrayOf(PolygonTunnel::class.qualifiedName))
//    PApplet.main(arrayOf(PolygonSpiralFlower::class.qualifiedName))
//    PApplet.main(arrayOf(InflatingCircles::class.qualifiedName))
//    PApplet.main(arrayOf(Explorer::class.qualifiedName))
//    PApplet.main(arrayOf(ExplorerMazeBuilder::class.qualifiedName))


/* ______________ SHOWCASE ______________ */
//    PApplet.main(arrayOf(PixelatedWalkers::class.qualifiedName))
//    PApplet.main(arrayOf(SmoothWalkers::class.qualifiedName))
//    PApplet.main(arrayOf(RaisingTiles::class.qualifiedName))
//    PApplet.main(arrayOf(SoupParticles::class.qualifiedName))
//    PApplet.main(arrayOf(MatrixLetters::class.qualifiedName))
//    PApplet.main(arrayOf(TextFromRandom::class.qualifiedName))
//    PApplet.main(arrayOf(NoisyWave::class.qualifiedName))
//    PApplet.main(arrayOf(NoisyWaveSurface::class.qualifiedName))
//    PApplet.main(arrayOf(Toothpicks::class.qualifiedName))
//    PApplet.main(arrayOf(RecamansSequence::class.qualifiedName))
//    PApplet.main(arrayOf(ZenSpiral::class.qualifiedName))
//    PApplet.main(arrayOf(AnimatedRosePatterns::class.qualifiedName))
//    PApplet.main(arrayOf(MaurerRosePatterns::class.qualifiedName))
//    PApplet.main(arrayOf(CircleTangents::class.qualifiedName))
//    PApplet.main(arrayOf(EveningExperiment1Idea2::class.qualifiedName))
//    PApplet.main(arrayOf(ConstraintNoise::class.qualifiedName))
//    PApplet.main(arrayOf(ConstrainedNoiseWithStencil::class.qualifiedName))
//    PApplet.main(arrayOf(GrowingNeuron2::class.qualifiedName))
//    PApplet.main(arrayOf(CirclePendulum2::class.qualifiedName))
//    PApplet.main(arrayOf(RainbowSunrise::class.qualifiedName))
}
