package ch.nych.soundtransmitter.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

/**
 * Created by nych on 4/13/16.
 */
public class Configuration {

    /**
    *
    */
    public final static String LOG_TAG = "BridgeAPI";

    /**
     * Uses two different frequencies for the transmission. Should only be used in combination with
     * a form of encoding like Manchester.
     */
    public final static int SINGLE_CHANNEL_TRANSMISSION = 3;

    /**
     * Uses a total of four different frequencies for the transmission (two per state).
     */
    public final static int TWO_CHANNEL_TRANSMISSION = 5;

    /**
     * Uses a total of six different frequencies for the transmission (three per state).
     */
    public final static int THREE_CHANNEL_TRANSMISSION = 9;

    /**
     *
     */
    public final static int SAMPLE_RATE_48KHZ = 48000;

    /**
     *
     */
    public final static int SAMPLE_RATE_44KHZ = 44100;

    /**
     * Minimum size of a tone impulse in samples.
     */
    public final static int MIN_TONE_SIZE = 60;

    /**
     * Maximum size of a tone impulse in samples.
     */
    public final static int MAX_TONE_SIZE = 48000;

    /**
     *
     */
    public final static double DEFAULT_FREQUENCY_RESOLUTION_FACTOR = 1;

    /**
     *
     */
    public final static int SINE_TONE = 1;

    /**
     * Minimum window size for the Goertzel algorithm.
     */
    public final static int MIN_WINDOW_SIZE = 60;

    /**
     *
     */
    public final static int DEFAULT_OVERLAPPING_FACTOR = 3;

    /**
     *
     */
    public final static int HAMMING_WINDOW = 1;

    /**
     *
     */
    public final static int HANN_WINDOW = 2;

    /**
     * Maximum number of windows per frame
     */
    public final static int DEFAULT_FRAME_SIZE = 1500;

    /**
     * Static threshold for the detection of a frame
     */
    public final static double DEFAULT_RECEIVER_THRESHOLD = 10000000.0;

    /**
     *
     */
    public final static int THRESHOLD_FUNCTION_ROOT_MEAN_SQUARE = 0;

    /**
     *
     */
    public final static int THRESHOLD_FUNCTION_ARITHMETIC_MEAN = 1;

    /**
     *
     */
    public final static int THRESHOLD_FUNCTION_MEDIAN = 2;


    /**
     * The default preamble for a frame
     */
    public final static byte[] DEFAULT_PREAMBLE = new byte[]{0, 1, 1};

    /**
     * Local log tag
     */
    private final String logTag = Configuration.LOG_TAG + ":Config";

    /**
     *
     */
    private int transmissionMode = 0;

    /**
     * Getter for transmission mode.
     * @return
     */
    public int getTransmissionMode() {
        return this.transmissionMode;
    }

    /**
     * Setter for transmission mode
     * @param transmissionMode
     * @return
     */
    public boolean setTransmissionMode(final int transmissionMode) {
        if(transmissionMode == Configuration.SINGLE_CHANNEL_TRANSMISSION ||
                transmissionMode == Configuration.TWO_CHANNEL_TRANSMISSION ||
                transmissionMode == Configuration.THREE_CHANNEL_TRANSMISSION) {
            this.transmissionMode = transmissionMode;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    private int toneType = 0;

    /**
     * Getter for tone type
     * @return
     */
    public int getToneType() {
        return this.toneType;
    }

    /**
     * Setter for tone type
     * @param toneType
     * @return
     */
    public boolean setToneType(final int toneType) {
        if(toneType == Configuration.SINE_TONE) {
            this.toneType = toneType;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    private int toneSize = 0;

    /**
     * Getter for tone size
     * @return
     */
    public int getToneSize() {
        return this.toneSize;
    }

    /**
     * Setter for tone size
     * @param toneSize
     * @return
     */
    public boolean setToneSize(final int toneSize) {
        if(toneSize < Configuration.MIN_TONE_SIZE) {
            Log.w(this.logTag, "Tone size can't be smaller than " +
                    Configuration.MIN_TONE_SIZE + " samples");
            return false;
        } else if(toneSize > Configuration.MAX_TONE_SIZE) {
            Log.w(this.logTag, "Tone sizes over " + Configuration.MAX_TONE_SIZE +
                    " samples are not allowed");
            return false;
        }
        this.toneSize = toneSize;
        return true;
    }

    /**
     *
     */
    private int controlToneSize = 0;

    /**
     * Getter for control tone size
     * @return
     */
    public int getControlToneSize() {
        return this.controlToneSize;
    }

    /**
     * Setter for control tone size
     * @param controlToneSize
     * @return
     */
    public boolean setControlToneSize(final int controlToneSize) {
        // TODO: 5/7/16 validation
        this.controlToneSize = controlToneSize;
        return true;
    }

    /**
     *
     */
    private int sampleRate = 0;

    /**
     * Getter for sample rate
     * @return
     */
    public int getSampleRate() {
        return this.sampleRate;
    }

    /**
     * Setter for sample rate
     * @param sampleRate
     * @return
     */
    public boolean setSampleRate(final int sampleRate) {
        if(sampleRate == Configuration.SAMPLE_RATE_48KHZ ||
                sampleRate == Configuration.SAMPLE_RATE_44KHZ) {
            this.sampleRate = sampleRate;
            this.calcBaseFrequency(this.baseFrequency);
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public double getNyquistFrequency() {
        return this.sampleRate / 2;
    }

    /**
     * The window size.
     */
    private int windowSize = 0;

    /**
     * Getter method for the window size. The window size defines the number of samples being
     * analyzed per time. This is an important value as in combination with the sample rate it
     * defines the resolution of the transformed data. The higher the resolution, the more
     * frequencies can be distinguished.
     * @return
     */
    public int getWindowSize() {
        return this.windowSize;
    }

    /**
     * Setter for window size
     * @param windowSize
     * @return
     */
    public boolean setWindowSize(final int windowSize) {
        if(windowSize < Configuration.MIN_WINDOW_SIZE) {
            Log.w(this.logTag, "Invalid Window size. Minimal size is: " +
                    Configuration.MIN_WINDOW_SIZE);
            return false;
        } else if(windowSize > this.sampleRate) {
            Log.w(this.logTag, "Invalid Window size. Maximal size is: " +
                    this.getSampleRate());
            return false;
        }
        this.windowSize = windowSize;
        this.calcBaseFrequency(this.baseFrequency);
        return true;
    }

    /**
     * The method returns the calculated frequency factor. The frequency factor is the delta between
     * the single carrier frequencies.
     * @return The frequency factor or frequency delta
     */
    public double getFrequencyResolution() {
        return (double) this.sampleRate / this.windowSize;
    }

    /**
     *
     */
    private double frequencyResolutionFactor = 0.0;

    /**
     * Getter for frequency resolution factor
     * @return
     */
    public double getFrequencyResolutionFactor() {
        return this.frequencyResolutionFactor;
    }

    /**
     * Setter for frequency resolution factor
     * @param frequencyResolutionFactor
     * @return
     */
    public boolean setFrequencyResolutionFactor(final double frequencyResolutionFactor) {
        if(frequencyResolutionFactor < 1) {
            Log.w(this.logTag, "Minimum for the resolution factor is 1");
            return false;
        }

        double frequencyDelta = frequencyResolutionFactor * this.getFrequencyResolution();

        if((frequencyDelta * this.transmissionMode + this.baseFrequency) >
                this.getNyquistFrequency()) {
            Log.w(this.logTag, "Reduce the factor, nyquist frequency exceeded.");
            return false;
        }

        this.frequencyResolutionFactor = frequencyResolutionFactor;
        return true;
    }

    /**
     * Getter for frequency delta
     * @return
     */
    public double getFrequencyDelta() {
        return frequencyResolutionFactor * this.getFrequencyResolution();
    }

    /**
     * The method calculated the base frequency upon an approximation value. Because the base
     * frequency and the resulting carrier frequencies should be integer multiples of the
     * frequency factor, it is useful to use this method. The reason lays in the maths of the
     * Goertzel algorithm, respectively the Discrete Fourier transform.
     * @param approximationValue The approximate frequency you want as base frequency.
     * @return The calculated base frequency based on your approximation value or -1 if the
     * approximationValue parameter was zero or below. If the base frequency and the carrier
     * frequencies above, exceed the nyquist frequency of (SAMPLE_RATE / 2), the return value is -2.
     */
    public double calcBaseFrequency(final double approximationValue) {
        if(approximationValue <= 0) {
            Log.w(this.logTag, "A calculation of a base frequency of zero or below is not allowed");
            return -1;
        }
        double baseFrequency;
        baseFrequency = (int) (approximationValue / this.getFrequencyResolution());
        baseFrequency *= this.getFrequencyResolution();
        return baseFrequency;
    }

    /**
     * The base frequency for the carrier frequency set.
     */
    private double baseFrequency = 0.0;

    /**
     * Getter method for the base frequency. This frequency is the base for the carrier frequencies.
     * The number of carrier frequencies above the base frequency, depends on the chosen
     * transmission mode.
     * @return The configured base frequency.
     */
    public double getBaseFrequency() {
        return this.baseFrequency;
    }

    /**
     * When setting the base frequency manually, always set the secure flag on true. The base
     * frequency will then be calculated for you. The reason is that the base frequency and the
     * resulting carrier frequencies should be integer multiples of the frequency factor. If you're
     * not setting the secureFlag, you should be careful. Choosing the wrong base frequency can and
     * will result in poor behavior of the Goertzel algorithm, respectively the Discrete Fourier
     * transform.
     * @param baseFrequency The base frequency of your carrier frequencies. Based on the
     *                      transmission mode you chose, a set of frequencies above the base
     *                      frequency will be used for data transmission. The calculation of these
     *                      frequencies is done in dependence of the window size and the sample
     *                      rate.
     * @param secureFlag This flag should always be turned on unless you want to force a specific
     *                   base frequency. If the flag is set to false, the baseFrequency parameter is
     *                   only checked against the nyquist frequency (SAMPLE_RATE / 2) and zero or
     *                   less. The base frequency will be set exactly to the baseFrequency
     *                   parameter.
     * @return True if the baseFrequency is bigger than zero and less than the nyquist frequency,
     * False otherwise.
     */
    public boolean setBaseFrequency(final double baseFrequency, final boolean secureFlag) {
        double nyquistLimit = this.getNyquistFrequency() -
                (this.getTransmissionMode() * this.getFrequencyDelta());
        if(baseFrequency <= 0) {
            Log.w(this.logTag, "Base frequency can't be zero or less");
            return false;
        } else if(baseFrequency > nyquistLimit) {
            Log.w(this.logTag, "Base frequency can't be higher than the nyquist" +
                    "frequency of: " + nyquistLimit);
            return false;
        }
        if(secureFlag) {
            this.baseFrequency = this.calcBaseFrequency(baseFrequency);
        } else {
            this.baseFrequency = baseFrequency;
        }
        return true;
    }

    /**
     * Getter for frequency set
     * @return
     */
    public double[] getFrequencies() {
        double[] frequencySet = new double[this.transmissionMode];
        double frequencyDelta = this.getFrequencyDelta();
        for (int i = 0; i < this.transmissionMode; i++) {
            frequencySet[i] = this.baseFrequency;
            frequencySet[i] += i * frequencyDelta;
        }
        return frequencySet;
    }


    /**
     * Is only used for the minimum of the sample buffer. The RecordingTask calculates its minimum
     * buffer size independently.
     */
    private int minBufferSize = 0;

    /**
     *
     */
    private int sampleBufferSize = 0;

    /**
     * Getter for sample buffer size
     * @return
     */
    public int getSampleBufferSize() {
        return this.sampleBufferSize;
    }

    /**
     * Setter for sample buffer size
     * @param sampleBufferSize
     * @return
     */
    public boolean setSampleBufferSize(final int sampleBufferSize) {
        if(sampleBufferSize < this.minBufferSize) {
            Log.w(this.logTag, "Invalid sampleBuffer size. Minimal size is: " + minBufferSize);
            return false;
        }
        this.sampleBufferSize = sampleBufferSize;
        return true;

    }

    /**
     *
     */
    private int overlappingFactor = 0;

    /**
     * Getter for overlapping factor
     * @return
     */
    public int getOverlappingFactor() {
        return this.overlappingFactor;
    }

    /**
     * Setter for overlapping factor
     * @param overlappingFactor
     * @return
     */
    public boolean setOverlappingFactor(final int overlappingFactor) {
        if(overlappingFactor < 1) {
            Log.w(this.logTag, "Invalid Overlapping factor. Can not be smaller than one");
            return false;
        }
        this.overlappingFactor = overlappingFactor;
        return true;
    }

    private int windowFunction = 0;

    /**
     * Getter for window function
     * @return
     */
    public int getWindowFunction() {
        return this.windowFunction;
    }

    /**
     * Getter for window function
     * @param windowFunction
     * @return
     */
    public boolean setWindowFunction(final int windowFunction) {
        if(windowFunction != Configuration.HAMMING_WINDOW ||
                windowFunction != Configuration.HANN_WINDOW) {
            Log.w(this.logTag, "Invalid window function. (If implemented a new one, you need to " +
                    "update the Configuration class");
            return false;
        }
        this.windowFunction = windowFunction;
        return true;
    }

    private int maxFrameSize = 0;

    /**
     * Setter for maximum frame size
     * @param maxFrameSize
     * @return
     */
    public boolean setMaxFrameSize(final int maxFrameSize) {
        // TODO: 4/24/16 Validation and Testcase
        this.maxFrameSize = maxFrameSize;
        return true;
    }

    /**
     * Getter for maximum frame size
     * @return
     */
    public int getMaxFrameSize() {
        return this.maxFrameSize;
    }

    private double receiverThreshold = 0.0;

    /**
     * Setter for receiver threshold
     * @param receiverThreshold
     * @return
     */
    public boolean setReceiverThreshold(final double receiverThreshold) {
        // TODO: 5/1/16 Validation and Testcase
        this.receiverThreshold = receiverThreshold;
        return true;
    }

    /**
     * Getter for receiver threshold
     * @return
     */
    public double getReceiverThreshold() {
        return this.receiverThreshold;
    }

    private int thresholdFunction = 0;

    /**
     * Setter for threshold function
     * @param thresholdFunction
     * @return
     */
    public boolean setThresholdFunction(final int thresholdFunction) {
        if(thresholdFunction != Configuration.THRESHOLD_FUNCTION_ROOT_MEAN_SQUARE ||
                thresholdFunction != Configuration.THRESHOLD_FUNCTION_ARITHMETIC_MEAN ||
                thresholdFunction != Configuration.THRESHOLD_FUNCTION_MEDIAN) {
            Log.w(this.logTag, "Invalid threshold function");
            return false;
        }
        this.thresholdFunction = thresholdFunction;
        return true;
    }

    /**
     * Setter for threshold function
     * @return
     */
    public int getThresholdFunction() {
        return this.thresholdFunction;
    }

    private double[] filterCoefficients = null;

    public double[] getFilterCoefficients() {
        return this.filterCoefficients;
    }

    public boolean setFilterCoefficients(final double[] filterCoeffizients) {
        this.filterCoefficients = filterCoeffizients;
        return true;
    }

    private byte[] preamble = null;

    /**
     * Only zero or one
     * @param preamble
     * @return
     */
    public boolean setPreamble(final byte[] preamble) {
        // TODO: 5/2/16 Validation and Testcase
        if(preamble == null) {
            Log.w(this.logTag, "Your preamble shouldn't be null. If no preamble is used, init" +
                    "with new byte[0]");
            this.preamble = new byte[0];
        }
        for(int i = 0; i < preamble.length; i++) {
            if(preamble[i] > 1) {
                Log.w(this.logTag, "Your preamble contains values unequal to zero or one");
                return false;
            }
        }
        this.preamble = preamble;
        return true;
    }

    /**
     * Getter for the preamble
     * @return
     */
    public byte[] getPreamble() {
        return this.preamble;
    }

    /**
     *
     */
    private Configuration() {};

    private static Configuration defaultBaseConfiguration() {
        Configuration configuration = new Configuration();
        configuration.transmissionMode = Configuration.TWO_CHANNEL_TRANSMISSION;
        configuration.sampleRate = Configuration.SAMPLE_RATE_48KHZ;
        configuration.toneType = Configuration.SINE_TONE;
        configuration.windowFunction = Configuration.HAMMING_WINDOW;
        configuration.minBufferSize = AudioRecord.getMinBufferSize(
                configuration.getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 10;
        configuration.sampleBufferSize = configuration.minBufferSize;
        configuration.frequencyResolutionFactor = Configuration.DEFAULT_FREQUENCY_RESOLUTION_FACTOR;
        configuration.overlappingFactor = Configuration.DEFAULT_OVERLAPPING_FACTOR;
        configuration.preamble = configuration.DEFAULT_PREAMBLE;
        configuration.filterCoefficients = new double[]{0.083,0.167, 0.5, 0.167, 0.083};
        configuration.maxFrameSize = Configuration.DEFAULT_FRAME_SIZE;
        configuration.receiverThreshold = configuration.DEFAULT_RECEIVER_THRESHOLD;
        return configuration;
    }

    /**
     *
     * @return
     */
    public static Configuration newUltrasonicConfiguration() {
        Configuration configuration = Configuration.defaultBaseConfiguration();
        configuration.windowSize = 120;
        configuration.toneSize = 240;
        configuration.controlToneSize = 480;
        configuration.baseFrequency = configuration.calcBaseFrequency(18000);
        return configuration;
    }

    /**
     *
     * @return
     */
    public static Configuration newAudibleConfiguration() {
        Configuration configuration = Configuration.defaultBaseConfiguration();
        configuration.windowSize = 2400;
        configuration.toneSize = 4800;
        configuration.controlToneSize = 9600;
        configuration.baseFrequency = configuration.calcBaseFrequency(500);
        return configuration;
    }
}
