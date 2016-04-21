package ch.nych.soundtransmitter.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

/**
 * Created by nych on 4/13/16.
 */
public class Configuration {
    /*
    *
    */
    public final static String LOG_TAG = "BridgeAPI";

    /*
     *
     */
    public final static int TWO_STATE_TRANSMISSION= 5;

    /*
     *
     */
    public final static int FOUR_STATE_TRANSMISSION = 9;

    /*
     *
     */
    public final static int SAMPLE_RATE_48KHZ = 48000;

    /*
     *
     */
    public final static int SAMPLE_RATE_44KHZ = 44100;

    /*
     *
     */
    public final static int MIN_TONE_SIZE = 480;


    public final static int MAX_TONE_SIZE = 48000;


    public final static double DEFAULT_FREQUENCY_RESOLUTION_FACTOR = 1;

    /*
     *
     */
    public final static double ULTRASONIC_BASE_FREQUENCY = 19000.0;

    /*
     *
     */
    public final static int SINE_TONE = 1;

    /* -------------------------------------------------------------------------------------------*/

    // TODO: 4/13/16 This might change with the usage of another transformation algorithm than goertzel
    /**
     *
     */
    public final static int MIN_WINDOW_SIZE = 480;

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

    //--------------------------------------------------------------------------------------------//

    /**
     *
     */
    private int transmissionMode = 0;

    public int getTransmissionMode() {
        return this.transmissionMode;
    }

    public boolean setTransmissionMode(final int transmissionMode) {
        if(transmissionMode == Configuration.TWO_STATE_TRANSMISSION ||
                transmissionMode == Configuration.FOUR_STATE_TRANSMISSION) {
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

    public int getToneType() {
        return this.toneType;
    }

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

    public int getToneSize() {
        return this.toneSize;
    }

    public boolean setToneSize(final int toneSize) {
        if(toneSize < Configuration.MIN_TONE_SIZE) {
            Log.w(Configuration.LOG_TAG, "Tone size can't be smaller than " +
                    Configuration.MIN_TONE_SIZE + " samples");
            return false;
        } else if(toneSize > Configuration.MAX_TONE_SIZE) {
            Log.w(Configuration.LOG_TAG, "Tone sizes over " + Configuration.MAX_TONE_SIZE +
                    " samples are not allowed");
            return false;
        }
        this.toneSize = toneSize;
        return true;
    }

    /**
     *
     */
    private int sampleRate = 0;

    public int getSampleRate() {
        return this.sampleRate;
    }

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
     *
     * @param windowSize
     * @return
     */
    public boolean setWindowSize(final int windowSize) {
        if(windowSize < Configuration.MIN_WINDOW_SIZE) {
            Log.w(Configuration.LOG_TAG, "Invalid Window size. Minimal size is: " +
                    Configuration.MIN_WINDOW_SIZE);
            return false;
        } else if(windowSize > this.sampleRate) {
            Log.w(Configuration.LOG_TAG, "Invalid Window size. Maximal size is: " +
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
     *
     * @return
     */
    public double getFrequencyResolutionFactor() {
        return this.frequencyResolutionFactor;
    }

    /**
     *
     * @param frequencyResolutionFactor
     * @return
     */
    public boolean setFrequencyResolutionFactor(final double frequencyResolutionFactor) {
        if(frequencyResolutionFactor < 1) {
            Log.w(Configuration.LOG_TAG, "Minimum for the resolution factor is 1");
            return false;
        }

        double frequencyDelta = frequencyResolutionFactor * this.getFrequencyResolution();

        if((frequencyDelta * this.transmissionMode + this.baseFrequency) >
                this.getNyquistFrequency()) {
            Log.w(Configuration.LOG_TAG, "Reduce the factor, nyquist frequency exceeded.");
            return false;
        }

        this.frequencyResolutionFactor = frequencyResolutionFactor;
        return true;
    }

    /**
     *
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
            Log.w(Configuration.LOG_TAG, "A calculation of a base frequency of zero or below is" +
                    "not allowed");
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
            Log.w(Configuration.LOG_TAG, "Base frequency can't be zero or less");
            return false;
        } else if(baseFrequency > nyquistLimit) {
            Log.w(Configuration.LOG_TAG, "Base frequency can't be higher than the nyquist" +
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
     *
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
     *
     */
    private int minBufferSize = 0;

    /**
     *
     */
    private int sampleBufferSize = 0;

    /**
     *
     * @return
     */
    public int getSampleBufferSize() {
        return this.sampleBufferSize;
    }

    /**
     *
     * @param sampleBufferSize
     * @return
     */
    public boolean setSampleBufferSize(final int sampleBufferSize) {
        if(sampleBufferSize < this.minBufferSize) {
            Log.w(Configuration.LOG_TAG, "Invalid sampleBuffer size. Minimal size is: " +
                    minBufferSize);
            return false;
        }
        this.sampleBufferSize = sampleBufferSize;
        return true;

    }

    /**
     *
     */
    private int overlappingFactor = 0;

    public int getOverlappingFactor() {
        return this.overlappingFactor;
    }

    public boolean setOverlappingFactor(final int overlappingFactor) {
        if(overlappingFactor < 1) {
            Log.w(Configuration.LOG_TAG, "Invalid Overlapping factor. Can not be smaller than one");
            return false;
        }
        this.overlappingFactor = overlappingFactor;
        return true;
    }

    private int windowFunction = 0;

    public int getWindowFunction() {
        return this.windowFunction;
    }

    public boolean setWindowFunction(final int windowFunction) {
        if(windowFunction != Configuration.HAMMING_WINDOW ||
                windowFunction != Configuration.HANN_WINDOW) {
            Log.w(Configuration.LOG_TAG, "Invalid window function. (If implemented a new one, you" +
                    "need to update the Configuration class");
            return false;
        }
        this.windowFunction = windowFunction;
        return true;
    }

    private Configuration() {};

    public static Configuration newUltrasonicConfiguration() {
        Configuration configuration = new Configuration();
        configuration.transmissionMode = Configuration.FOUR_STATE_TRANSMISSION;

        configuration.toneType = Configuration.SINE_TONE;
        configuration.toneSize = Configuration.MIN_TONE_SIZE;
        configuration.sampleRate = Configuration.SAMPLE_RATE_48KHZ;
        configuration.baseFrequency = Configuration.ULTRASONIC_BASE_FREQUENCY;
        configuration.windowSize = Configuration.MIN_WINDOW_SIZE;
        configuration.frequencyResolutionFactor = Configuration.DEFAULT_FREQUENCY_RESOLUTION_FACTOR;
        configuration.minBufferSize = AudioRecord.getMinBufferSize(
                configuration.getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 10;
        configuration.sampleBufferSize = configuration.minBufferSize;

        configuration.overlappingFactor = Configuration.DEFAULT_OVERLAPPING_FACTOR;
        configuration.windowFunction = Configuration.HANN_WINDOW;
        return configuration;
    }
}
