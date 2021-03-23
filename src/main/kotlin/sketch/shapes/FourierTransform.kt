package sketch.shapes

import BaseSketch
import Screen
import processing.core.PVector
import kotlin.math.roundToInt

class FourierTransform : BaseSketch(Screen(1500, 900), renderer = P2D) {

    private val yCircleX = -screen.halfWidth
    private val yCircleY = 0f
    private val xCircleX = 0f
    private val xCircleY = -screen.halfHeight
    private val drawingOffset = 0f

    private val shape = mutableListOf<PVector>()
    private lateinit var fourierX: List<Signal>
    private lateinit var fourierY: List<Signal>
    private var time = 0f

    private val squareMin = -150f
    private val squareMax = 150f

    private val square = List(200) {
        when {
            it < 50 -> {
                val x = map(it.toFloat(), 0f, 49f, squareMin, squareMax)
                PVector(x, squareMin)
            }
            it < 100 -> {
                val y = map(it.toFloat(), 50f, 99f, squareMin, squareMax)
                PVector(squareMax, y)
            }
            it < 150 -> {
                val x = map(it.toFloat(), 100f, 149f, squareMax, squareMin)
                PVector(x, squareMax)
            }
            else -> {
                val y = map(it.toFloat(), 150f, 199f, squareMax, squareMin)
                PVector(squareMin, y)
            }
        }
    }

    private val circle = List(200) {
        val angle = map(it.toFloat(), 0f, 199f, 0f, PI)
        val radius = 150f
        val x = radius * cos(angle)
        val y = radius * sin(angle)
        PVector(x, y)
    }
    private val input = circle
    private val dt = TWO_PI / input.size

    override fun setup() {
        frameRate(10f)
        fourierX = dft(input
//            .filterIndexed { index, _ -> index % 2 == 0 }
            .map { it.x })
            .sortedWith { a, b -> (b.amp - a.amp).roundToInt() }
        fourierY = dft(input
//            .filterIndexed { index, _ -> index % 2 == 1 }
            .map { it.y })
            .sortedWith { a, b -> (b.amp - a.amp).roundToInt() }
    }

    override fun draw() {
        background(grey3)
        translate((3f / 5f) * screen.widthF, (3f / 4f) * screen.heightF)

        stroke(red)
        strokeWeight(5f)
        point(0f, 0f)
        point(yCircleX, yCircleY)
        strokeWeight(1f)

        noFill()
        stroke(grey9)

        val xComponent = epicircles(xCircleX, xCircleY, 0f, fourierX)
        val yComponent = epicircles(yCircleX, yCircleY, HALF_PI, fourierY)
        val resultVertex = PVector(
            xComponent.x,
            yComponent.y
        )

        line(xCircleX + xComponent.x, xCircleY + xComponent.y, resultVertex.x, resultVertex.y)
        line(yCircleX + yComponent.x, yCircleY + yComponent.y, resultVertex.x, resultVertex.y)
        shape.add(resultVertex)
        drawShape()

        time += dt

        if (time > 2 * TWO_PI) {
            time = 0f
            shape.clear()
        }
    }

    private fun dft(input: List<Float>): List<Signal> {
        val result = mutableListOf<Signal>()
        for (k in input.indices) {
            var re = 0f
            var im = 0f
            for (n in input.indices) {
                val theta = (TWO_PI * k * n) / input.size
                re += input[n] * cos(theta)
                im -= input[n] * sin(theta)
            }
            re /= input.size
            im /= input.size
            val amp = sqrt(sq(re) + sq(im))
            val freq = k.toFloat()
            val phase = atan2(im, re)
            result.add(k, Signal(re, im, amp, freq, phase))
        }
        return result
    }

    private fun epicircles(
        xOffset: Float,
        yOffset: Float,
        rotation: Float,
        fourier: List<Signal>
    ): PVector {
        var x = 0f
        var y = 0f
        var circleX: Float
        var circleY: Float
        for (i in fourier.indices) {
            circleX = xOffset + x
            circleY = yOffset + y
            val freq = fourier[i].freq
            val radius = fourier[i].amp
            val phase = fourier[i].phase
            x += radius * cos(freq * time + phase + rotation)
            y += radius * sin(freq * time + phase + rotation)
            stroke(grey5)
            circle(circleX, circleY, radius * 2)
            stroke(grey11)
            line(circleX, circleY, xOffset + x, yOffset + y)
//            point(x, y)
        }
        return PVector(x, y)
    }

    private fun drawShape() {
        beginShape()
        for (i in 0 until shape.size) {
            val x = drawingOffset + shape[i].x
            val y = drawingOffset + shape[i].y
            vertex(x, y)
        }
        endShape()
    }

    class Signal(
        val re: Float,
        val im: Float,
        val amp: Float,
        val freq: Float,
        val phase: Float
    ) {
        override fun toString(): String {
            return "[\n\tre: $re,\n" +
                    "\tim: $im,\n" +
                    "\tamp: $amp,\n" +
                    "\tfreq: $freq,\n" +
                    "\tphase: $phase]"
        }
    }
}

private val prsoid = listOf(
    PVector(-200.0f, -1.7484555E-5f),
    PVector(-199.99f, -1.9999822f),
    PVector(-199.96f, -3.999747f),
    PVector(-199.91f, -5.9991117f),
    PVector(-199.84003f, -7.997876f),
    PVector(-199.75006f, -9.995842f),
    PVector(-199.6401f, -11.992807f),
    PVector(-199.51021f, -13.988574f),
    PVector(-199.36035f, -15.982941f),
    PVector(-199.19055f, -17.97571f),
    PVector(-199.00084f, -19.96668f),
    PVector(-198.79121f, -21.955658f),
    PVector(-198.56172f, -23.942436f),
    PVector(-198.31238f, -25.926823f),
    PVector(-198.0432f, -27.908615f),
    PVector(-197.75421f, -29.887617f),
    PVector(-197.44547f, -31.86363f),
    PVector(-197.11696f, -33.836452f),
    PVector(-196.76874f, -35.8059f),
    PVector(-196.40085f, -37.771763f),
    PVector(-196.01332f, -39.733845f),
    PVector(-195.60619f, -41.69196f),
    PVector(-195.1795f, -43.6459f),
    PVector(-194.73329f, -45.595478f),
    PVector(-194.26761f, -47.540497f),
    PVector(-193.7825f, -49.480762f),
    PVector(-193.278f, -51.41608f),
    PVector(-192.75418f, -53.346252f),
    PVector(-192.2111f, -55.271095f),
    PVector(-191.64879f, -57.190407f),
    PVector(-191.06732f, -59.104f),
    PVector(-190.46672f, -61.01169f),
    PVector(-189.8471f, -62.91327f),
    PVector(-189.20848f, -64.80856f),
    PVector(-188.55095f, -66.69737f),
    PVector(-187.87456f, -68.57951f),
    PVector(-187.17938f, -70.4548f),
    PVector(-186.46548f, -72.32304f),
    PVector(-185.73296f, -74.184044f),
    PVector(-184.98184f, -76.03763f),
    PVector(-184.21222f, -77.88361f),
    PVector(-183.4242f, -79.72181f),
    PVector(-182.61781f, -81.55203f),
    PVector(-181.79318f, -83.3741f),
    PVector(-180.95035f, -85.187836f),
    PVector(-180.08945f, -86.99304f),
    PVector(-179.21053f, -88.78956f),
    PVector(-178.31369f, -90.577194f),
    PVector(-177.39902f, -92.35577f),
    PVector(-176.46661f, -94.12511f),
    PVector(-175.51654f, -95.88504f),
    PVector(-174.54893f, -97.63538f),
    PVector(-173.56387f, -99.37595f),
    PVector(-172.56146f, -101.1066f),
    PVector(-171.54178f, -102.82712f),
    PVector(-170.50494f, -104.53737f),
    PVector(-169.45107f, -106.23716f),
    PVector(-168.38025f, -107.92633f),
    PVector(-167.29259f, -109.604706f),
    PVector(-166.18819f, -111.27212f),
    PVector(-165.06717f, -112.92841f),
    PVector(-163.92967f, -114.57341f),
    PVector(-162.77576f, -116.206955f),
    PVector(-161.60556f, -117.82887f),
    PVector(-160.41922f, -119.439f),
    PVector(-159.21683f, -121.0372f),
    PVector(-157.99852f, -122.62329f),
    PVector(-156.7644f, -124.19711f),
    PVector(-155.5146f, -125.758514f),
    PVector(-154.24927f, -127.30734f),
    PVector(-152.96852f, -128.84344f),
    PVector(-151.67244f, -130.36665f),
    PVector(-150.36122f, -131.87685f),
    PVector(-149.03496f, -133.37384f),
    PVector(-147.6938f, -134.85748f),
    PVector(-146.33786f, -136.32767f),
    PVector(-144.9673f, -137.7842f),
    PVector(-143.58223f, -139.22694f),
    PVector(-142.1828f, -140.65579f),
    PVector(-140.76917f, -142.07056f),
    PVector(-139.34143f, -143.47112f),
    PVector(-137.89978f, -144.85733f),
    PVector(-136.44434f, -146.22908f),
    PVector(-134.97525f, -147.58618f),
    PVector(-133.49268f, -148.92853f),
    PVector(-131.99673f, -150.256f),
    PVector(-130.48758f, -151.56845f),
    PVector(-128.96535f, -152.86575f),
    PVector(-127.43024f, -154.14777f),
    PVector(-125.88238f, -155.41438f),
    PVector(-124.32192f, -156.66544f),
    PVector(-122.74904f, -157.90082f),
    PVector(-121.163895f, -159.12042f),
    PVector(-119.56662f, -160.32413f),
    PVector(-117.95739f, -161.51178f),
    PVector(-116.33636f, -162.68329f),
    PVector(-114.703705f, -163.83852f),
    PVector(-113.05957f, -164.97737f),
    PVector(-111.40413f, -166.09973f),
    PVector(-109.737564f, -167.20546f),
    PVector(-108.060005f, -168.2945f),
    PVector(-106.37164f, -169.36668f),
    PVector(-104.672646f, -170.42194f),
    PVector(-102.96319f, -171.46014f),
    PVector(-101.24342f, -172.48122f),
    PVector(-99.51354f, -173.48503f),
    PVector(-97.773705f, -174.4715f),
    PVector(-96.02409f, -175.44052f),
    PVector(-94.26487f, -176.392f),
    PVector(-92.49623f, -177.32582f),
    PVector(-90.71834f, -178.24191f),
    PVector(-88.931366f, -179.1402f),
    PVector(-87.135506f, -180.02055f),
    PVector(-85.33093f, -180.88292f),
    PVector(-83.51782f, -181.72719f),
    PVector(-81.696365f, -182.55328f),
    PVector(-79.86674f, -183.36113f),
    PVector(-78.02912f, -184.15063f),
    PVector(-76.1837f, -184.92172f),
    PVector(-74.330666f, -185.67432f),
    PVector(-72.47019f, -186.40834f),
    PVector(-70.60248f, -187.12373f),
    PVector(-68.7277f, -187.8204f),
    PVector(-66.84605f, -188.49829f),
    PVector(-64.95771f, -189.15733f),
    PVector(-63.06287f, -189.79745f),
    PVector(-61.16173f, -190.4186f),
    PVector(-59.25448f, -191.0207f),
    PVector(-57.341297f, -191.6037f),
    PVector(-55.42238f, -192.16753f),
    PVector(-53.49792f, -192.71216f),
    PVector(-51.568108f, -193.23749f),
    PVector(-49.633144f, -193.74352f),
    PVector(-47.69322f, -194.23016f),
    PVector(-45.74852f, -194.69739f),
    PVector(-43.799244f, -195.14514f),
    PVector(-41.845592f, -195.57338f),
    PVector(-39.887753f, -195.98206f),
    PVector(-37.925926f, -196.37114f),
    PVector(-35.960304f, -196.74059f),
    PVector(-33.99109f, -197.09035f),
    PVector(-32.018475f, -197.42041f),
    PVector(-30.042658f, -197.73071f),
    PVector(-28.063837f, -198.02127f),
    PVector(-26.082209f, -198.292f),
    PVector(-24.097971f, -198.54292f),
    PVector(-22.111324f, -198.77397f),
    PVector(-20.122467f, -198.98514f),
    PVector(-18.131598f, -199.17642f),
    PVector(-16.138914f, -199.34776f),
    PVector(-14.144617f, -199.4992f),
    PVector(-12.148905f, -199.63066f),
    PVector(-10.1519785f, -199.74217f),
    PVector(-8.1540365f, -199.83371f),
    PVector(-6.155279f, -199.90526f),
    PVector(-4.1559067f, -199.95682f),
    PVector(-2.1561182f, -199.98837f),
    PVector(-0.15611409f, -199.99994f),
    PVector(-8.742278E-7f, -200.0f),
    PVector(1.996668f, -200.09991f),
    PVector(3.9733865f, -200.39867f),
    PVector(5.910405f, -200.89326f),
    PVector(7.7883677f, -201.57878f),
    PVector(9.588512f, -202.44835f),
    PVector(11.2928505f, -203.49329f),
    PVector(12.884356f, -204.70316f),
    PVector(14.347124f, -206.06587f),
    PVector(15.666541f, -207.56781f),
    PVector(16.829422f, -209.19395f),
    PVector(17.82415f, -210.92809f),
    PVector(18.640783f, -212.75285f),
    PVector(19.271166f, -214.65002f),
    PVector(19.708994f, -216.60066f),
    PVector(19.9499f, -218.58527f),
    PVector(19.991472f, -220.584f),
    PVector(19.833296f, -222.57689f),
    PVector(19.476952f, -224.54405f),
    PVector(18.926f, -226.46579f),
    PVector(18.185947f, -228.32294f),
    PVector(17.264187f, -230.09692f),
    PVector(16.169926f, -231.77002f),
    PVector(14.914102f, -233.32553f),
    PVector(13.50926f, -234.74788f),
    PVector(11.969439f, -236.02287f),
    PVector(10.310024f, -237.13777f),
    PVector(8.547593f, -238.08145f),
    PVector(6.699758f, -238.84445f),
    PVector(4.784981f, -239.41916f),
    PVector(2.8223941f, -239.79985f),
    PVector(0.83160645f, -239.9827f),
    PVector(-1.1674901f, -239.9659f),
    PVector(-3.1549215f, -239.74959f),
    PVector(-5.1108303f, -239.33597f),
    PVector(-7.0156727f, -238.72913f),
    PVector(-8.850417f, -237.93517f),
    PVector(-10.596729f, -236.96199f),
    PVector(-12.237163f, -235.81935f),
    PVector(-13.755325f, -234.51865f),
    PVector(-15.136051f, -233.07288f),
    PVector(-16.365543f, -231.49648f),
    PVector(-17.431515f, -229.80522f),
    PVector(-18.323317f, -228.01599f),
    PVector(-19.03204f, -226.14667f),
    PVector(-19.550602f, -224.21593f),
    PVector(-19.87382f, -222.24306f),
    PVector(-19.998465f, -220.24779f),
    PVector(-19.923294f, -218.25003f),
    PVector(-19.649055f, -216.26978f),
    PVector(-19.17849f, -214.32677f),
    PVector(-18.5163f, -212.44046f),
    PVector(-17.669104f, -210.62968f),
    PVector(-16.64536f, -208.91254f),
    PVector(-15.455305f, -207.30617f),
    PVector(-14.1108265f, -205.82663f),
    PVector(-12.625352f, -204.4887f),
    PVector(-11.013733f, -203.30576f),
    PVector(-9.2920685f, -202.28963f),
    PVector(-7.477561f, -201.45044f),
    PVector(-5.5883408f, -200.7966f),
    PVector(-3.6432834f, -200.33464f),
    PVector(-1.6618237f, -200.06915f),
    PVector(2.3849761E-6f, -200.0f),
    PVector(2.0000148f, -199.99f),
    PVector(3.9998274f, -199.96f),
    PVector(5.9992394f, -199.91f),
    PVector(7.998052f, -199.84001f),
    PVector(9.996065f, -199.75005f),
    PVector(11.993078f, -199.64009f),
    PVector(13.988892f, -199.51018f),
    PVector(15.983306f, -199.3603f),
    PVector(17.976124f, -199.1905f),
    PVector(19.967142f, -199.0008f),
    PVector(21.956163f, -198.79115f),
    PVector(23.94299f, -198.56166f),
    PVector(25.927422f, -198.3123f),
    PVector(27.909262f, -198.0431f),
    PVector(29.888308f, -197.7541f),
    PVector(31.864367f, -197.44534f),
    PVector(33.83724f, -197.11682f),
    PVector(35.80673f, -196.7686f),
    PVector(37.772636f, -196.40068f),
    PVector(39.734764f, -196.01312f),
    PVector(41.69292f, -195.60599f),
    PVector(43.64691f, -195.17928f),
    PVector(45.59653f, -194.73303f),
    PVector(47.541595f, -194.26733f),
    PVector(49.481903f, -193.7822f),
    PVector(51.417263f, -193.2777f),
    PVector(53.34748f, -192.75385f),
    PVector(55.272366f, -192.21074f),
    PVector(57.19172f, -191.6484f),
    PVector(59.105354f, -191.0669f),
    PVector(61.013077f, -190.46628f),
    PVector(62.914703f, -189.84662f),
    PVector(64.810036f, -189.20798f),
    PVector(66.69889f, -188.55042f),
    PVector(68.58107f, -187.874f),
    PVector(70.45639f, -187.17877f),
    PVector(72.32467f, -186.46486f),
    PVector(74.185715f, -185.73228f),
    PVector(76.03934f, -184.98112f),
    PVector(77.88536f, -184.21149f),
    PVector(79.72359f, -183.42342f),
    PVector(81.55385f, -182.617f),
    PVector(83.37595f, -181.79233f),
    PVector(85.18972f, -180.94948f),
    PVector(86.994965f, -180.08852f),
    PVector(88.791504f, -179.20956f),
    PVector(90.57918f, -178.31268f),
    PVector(92.35779f, -177.39796f),
    PVector(94.12716f, -176.46552f),
    PVector(95.887115f, -175.51541f),
    PVector(97.63749f, -174.54776f),
    PVector(99.3781f, -173.56265f),
    PVector(101.108765f, -172.5602f),
    PVector(102.82932f, -171.54047f),
    PVector(104.5396f, -170.50359f),
    PVector(106.23941f, -169.44966f),
    PVector(107.92861f, -168.37878f),
    PVector(109.607f, -167.29108f),
    PVector(111.274445f, -166.18663f),
    PVector(112.93076f, -165.06558f),
    PVector(114.57578f, -163.92801f),
    PVector(116.20934f, -162.77403f),
    PVector(117.831276f, -161.60379f),
    PVector(119.44144f, -160.4174f),
    PVector(121.03965f, -159.21497f),
    PVector(122.625755f, -157.9966f),
    PVector(124.19961f, -156.76244f),
    PVector(125.76103f, -155.51259f),
    PVector(127.30987f, -154.2472f),
    PVector(128.846f, -152.96637f),
    PVector(130.36922f, -151.67026f),
    PVector(131.87941f, -150.35898f),
    PVector(133.37642f, -149.03265f),
    PVector(134.86009f, -147.69142f),
    PVector(136.33028f, -146.33543f),
    PVector(137.78682f, -144.9648f),
    PVector(139.22958f, -143.57968f),
    PVector(140.65843f, -142.18019f),
    PVector(142.0732f, -140.7665f),
    PVector(143.47377f, -139.33871f),
    PVector(144.86f, -137.897f),
    PVector(146.23172f, -136.4415f),
    PVector(147.58884f, -134.97235f),
    PVector(148.9312f, -133.4897f),
    PVector(150.25865f, -131.9937f),
    PVector(151.57108f, -130.48451f),
    PVector(152.86835f, -128.96227f),
    PVector(154.15034f, -127.427124f),
    PVector(155.41692f, -125.87924f),
    PVector(156.66795f, -124.31876f),
    PVector(157.9033f, -122.74586f),
    PVector(159.12288f, -121.160675f),
    PVector(160.32654f, -119.56338f),
    PVector(161.51416f, -117.954124f),
    PVector(162.68564f, -116.33308f),
    PVector(163.84084f, -114.7004f),
    PVector(164.97964f, -113.056244f),
    PVector(166.10197f, -111.40078f),
    PVector(167.20769f, -109.73418f),
    PVector(168.29668f, -108.0566f),
    PVector(169.36882f, -106.36823f),
    PVector(170.42404f, -104.66921f),
    PVector(171.46222f, -102.95973f),
    PVector(172.48325f, -101.239944f),
    PVector(173.48705f, -99.51004f),
    PVector(174.47346f, -97.77019f),
    PVector(175.44246f, -96.020546f),
    PVector(176.39389f, -94.261314f),
    PVector(177.3277f, -92.492645f),
    PVector(178.24376f, -90.71474f),
    PVector(179.142f, -88.92775f),
    PVector(180.02232f, -87.13187f),
    PVector(180.88464f, -85.32728f),
    PVector(181.72888f, -83.51415f),
    PVector(182.55493f, -81.69268f),
    PVector(183.36275f, -79.86304f),
    PVector(184.1522f, -78.0254f),
    PVector(184.92325f, -76.17997f),
    PVector(185.67583f, -74.32691f),
    PVector(186.4098f, -72.46643f),
    PVector(187.12515f, -70.598694f),
    PVector(187.8218f, -68.72391f),
    PVector(188.49963f, -66.84223f),
    PVector(189.15865f, -64.95389f),
    PVector(189.79872f, -63.059036f),
    PVector(190.41983f, -61.157887f),
    PVector(191.0219f, -59.250618f),
    PVector(191.60486f, -57.337425f),
    PVector(192.16866f, -55.4185f),
    PVector(192.71323f, -53.49403f),
    PVector(193.23853f, -51.56421f),
    PVector(193.74452f, -49.629234f),
    PVector(194.23112f, -47.689293f),
    PVector(194.6983f, -45.74459f),
    PVector(195.14603f, -43.795307f),
    PVector(195.57422f, -41.841644f),
    PVector(195.98286f, -39.883797f),
    PVector(196.3719f, -37.921963f),
    PVector(196.7413f, -35.956337f),
    PVector(197.09103f, -33.98711f),
    PVector(197.42105f, -32.01449f),
    PVector(197.73132f, -30.038664f),
    PVector(198.02184f, -28.059837f),
    PVector(198.29253f, -26.078203f),
    PVector(198.5434f, -24.093962f),
    PVector(198.77441f, -22.107311f),
    PVector(198.98555f, -20.11845f),
    PVector(199.17679f, -18.127575f),
    PVector(199.3481f, -16.13489f),
    PVector(199.49948f, -14.140588f),
    PVector(199.63092f, -12.144874f),
    PVector(199.74237f, -10.147945f),
    PVector(199.83386f, -8.150002f),
    PVector(199.90538f, -6.151243f),
    PVector(199.95691f, -4.1518693f),
    PVector(199.98842f, -2.1520803f),
    PVector(199.99994f, -0.15207607f)
).filterIndexed { index, _ -> index % 2 == 0 }
