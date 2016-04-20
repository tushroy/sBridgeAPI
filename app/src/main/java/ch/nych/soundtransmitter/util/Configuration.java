package ch.nych.soundtransmitter.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
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
    public final static int TWO_STATE_TRANSMISSION= 2;

    /*
     *
     */
    public final static int FOUR_STATE_TRANSMISSION = 4;

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
    public final static int DEFAULT_TONE_SIZE = 480;

    /*
     *
     */
    public final static double ULTRASONIC_BASE_FREQUENCY = 19000.0;

    /*
     *
     */
    public final static int SINE_TONE = 1;

    /* -------------------------------------------------------------------------------------------*/
    /*                                         AudioRecord                                        */

    /**
     *
     */
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    /**
     *
     */
    public final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    /**
     *
     */
    public final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

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
        // TODO: 4/12/16 argument validation
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
            this.frequencyFactor = this.sampleRate / this.windowSize;
            this.calcBaseFrequency(this.baseFrequency);
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    private int windowSize = 0;

    public int getWindowSize() {
        return this.windowSize;
    }

    public boolean setWindowSize(final int windowSize) {
        if(windowSize < Configuration.MIN_WINDOW_SIZE) {
            Log.w(Configuration.LOG_TAG, "Invalid Window size. Minimal size is: " +
                    Configuration.MIN_WINDOW_SIZE);
            return false;
        }
        this.windowSize = windowSize;
        this.frequencyFactor = this.sampleRate / this.windowSize;
        this.calcBaseFrequency(this.baseFrequency);
        return true;
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
        double nyquistLimit = this.sampleRate / 2;
        nyquistLimit -= this.getFrequencies().length * this.frequencyFactor;
        if(baseFrequency <= 0) {
            Log.w(Configuration.LOG_TAG, "Base frequency can't be zero or less");
            return false;
        } else if(baseFrequency >= nyquistLimit) {
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
        double nyquistLimit = this.sampleRate / 2;
        nyquistLimit -= this.getFrequencies().length * this.frequencyFactor;

        if(baseFrequency <= 0) {
            Log.w(Configuration.LOG_TAG, "A calculation of a base frequency of zero or below is" +
                    "not allowed");
            return -1;
        } else if(baseFrequency >= nyquistLimit) {
            Log.w(Configuration.LOG_TAG, "A calculation of a base frequency higher than the" +
                    "nyquist frequency of: " + nyquistLimit + " is not allowed.");
            return -2;
        }

        double baseFrequency;
        baseFrequency = (int) approximationValue / this.frequencyFactor;
        baseFrequency *= this.frequencyFactor;
        return baseFrequency;
    }

    /**
     * The frequency factor is the delta between the single carrier frequencies. It should be
     * calculated by the formula SAMPLE_RATE / WINDOW_SIZE.
     */
    private double frequencyFactor = 0.0;

    /**
     * The method returns the calculated frequency factor. The frequency factor is the delta between
     * the single carrier frequencies.
     * @return The frequency factor or frequency delta
     */
    public double getFrequencyFactor() {
        if(this.frequencyFactor <= 0.0) {
           this.frequencyFactor = this.sampleRate / this.windowSize;
        }
        return this.frequencyFactor;
    }

    /**
     * If you don't exactly what you are doing, don't set the frequency factor manually. This
     * factor or delta between the single carrier frequencies is calculated with the formula
     * SAMPLE_RATE / WINDOW_SIZE. For the Goertzel algorithm, respectively the Discrete Fourier
     * transform, it is important, that the carrier frequencies are a integer multiple of this
     * factor.
     * By setting the force flag, the frequencyFactor is only checked against zero. It is not
     * guaranteed that the application will work successfully.
     *
     * @param frequencyFactor The delta between the single carrier frequencies
     * @return  True if the force flag is set and the frequencyFactor is bigger than zero. Otherwise
     * the return value is false.
     */
    public boolean setFrequencyFactor(final double frequencyFactor, final boolean force) {
        if(force && frequencyFactor > 0) {
            this.frequencyFactor = frequencyFactor;
            return true;
        }
        return false;
    }

    /**
     *
     */
    private double[] frequencySet = null;

    public double[] getFrequencies() {
        if(this.frequencySet == null) {
            int numberOfStates = this.transmissionMode * 2 + 1;
            this.frequencySet = new double[numberOfStates];

            for (int i = 0; i < numberOfStates; i++) {
                frequencySet[i] = this.baseFrequency;
                frequencySet[i] += i * this.frequencyFactor;
            }
        }
        return this.frequencySet;
    }

    /**
     *
     */
    private int audioSource = 0;

    public int getAudioSource() {
        return this.audioSource;
    }

    public boolean setAudioSource(final int audioSource) {
        // TODO: 4/19/16  Validate if there are other possible audio source for our purpose
        if(audioSource != MediaRecorder.AudioSource.MIC) {
            Log.w(Configuration.LOG_TAG, "Invalid AudioSource");
            return false;
        }
        this.audioSource = audioSource;
        return true;
    }

    /**
     *
     */
    private int channelConfig = 0;

    public int getChannelConfig() {
        return this.channelConfig;
    }

    public boolean setChannelConfig(final int channelConfig) {
        // TODO: 4/13/16 Validate if there are other possible channel configurations for our purpose
        if(channelConfig != AudioFormat.CHANNEL_IN_MONO) {
            Log.w(Configuration.LOG_TAG, "Invalid ChannelConfig");
            return false;
        }
        this.channelConfig = channelConfig;
        return true;
    }

    /**
     *
     */
    private int audioFormat = 0;

    public int getAudioFormat() {
        return this.audioFormat;
    }

    public boolean setAudioFormat(final int audioFormat) {
        if(audioFormat != AudioFormat.ENCODING_PCM_16BIT) {
            Log.w(Configuration.LOG_TAG, "Invalid AudioFormat, only 16 Bit PCM accepted");
            return false;
        }
        this.audioFormat = audioFormat;
        return true;
    }

    /**
     *
     */
    private int audioRecordBufferSize = 0;

    public int getAudioRecordBufferSize() {
        return this.audioRecordBufferSize;
    }

    public int getMinimumAudioRecordBufferSize() {
        int size = AudioRecord.getMinBufferSize(
                this.sampleRate,
                this.channelConfig,
                this.audioFormat);
        /*
         * division is necessary because getMinBufferSize() returns the minBufferSize in bytes
         * instead of shorts
         */
        return size / 2;
    }

    public boolean setAudioRecordBufferSize(final int audioRecordBufferSize) {
        if(audioRecordBufferSize < this.getMinimumAudioRecordBufferSize()) {
            Log.w(Configuration.LOG_TAG, "Invalid AudioRecord Buffer size. Minimal size is: " +
                    this.getMinimumAudioRecordBufferSize());
            return false;
        }
        // TODO: 4/19/16 This argument should also be checked against a max value
        // TODO: 4/19/16 If buffersize is raised, the samplebuffersize also needs to be resized
        this.audioRecordBufferSize = audioRecordBufferSize;
        return true;
    }

    /**
     *
     */
    private int sampleBufferSize = 0;

    public int getSampleBufferSize() {
        return this.sampleBufferSize;
    }

    public boolean setSampleBufferSize(final int sampleBufferSize) {
        int minBufferSize = this.audioRecordBufferSize * 10;
        if(sampleBufferSize < minBufferSize) {
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
        configuration.toneSize = Configuration.DEFAULT_TONE_SIZE;
        configuration.sampleRate = Configuration.SAMPLE_RATE_48KHZ;
        configuration.baseFrequency = Configuration.ULTRASONIC_BASE_FREQUENCY;
        configuration.windowSize = Configuration.MIN_WINDOW_SIZE;
        configuration.frequencyFactor = configuration.getFrequencyFactor();
        configuration.audioSource = Configuration.AUDIO_SOURCE;
        configuration.channelConfig = Configuration.CHANNEL_CONFIG;
        configuration.audioFormat = Configuration.AUDIO_FORMAT;
        configuration.audioRecordBufferSize = configuration.getMinimumAudioRecordBufferSize();
        configuration.sampleBufferSize = configuration.getAudioRecordBufferSize() * 10;

        configuration.overlappingFactor = Configuration.DEFAULT_OVERLAPPING_FACTOR;
        configuration.windowFunction = Configuration.HANN_WINDOW;
        return configuration;
    }
}
