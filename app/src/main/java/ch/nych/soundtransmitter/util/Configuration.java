package ch.nych.soundtransmitter.util;

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
    public final static double DEFAULT_FREQUENCY_DELTA = 100.0;

    /*
     *
     */
    public final static int SINE_TONE = 1;

    //--------------------------------------------------------------------------------------------//

    /**
     *
     */
    private int transmissionMode = 0;

    /**
     *
     */
    private int toneType = 0;

    /**
     *
     */
    private int toneSize = 0;

    /**
     *
     */
    private int sampleRate = 0;

    /**
     *
     */
    private double baseFrequency = 0.0;

    /**
     *
     */
    private double frequencyDelta = 0.0;

    private Configuration() {};

    public static Configuration newUltrasonicConfiguration() {
        Configuration configuration = new Configuration();
        configuration.transmissionMode = Configuration.FOUR_STATE_TRANSMISSION;
        configuration.toneType = Configuration.SINE_TONE;
        configuration.toneSize = Configuration.DEFAULT_TONE_SIZE;
        configuration.sampleRate = Configuration.SAMPLE_RATE_48KHZ;
        configuration.baseFrequency = Configuration.ULTRASONIC_BASE_FREQUENCY;
        configuration.frequencyDelta = Configuration.DEFAULT_FREQUENCY_DELTA;
        return configuration;
    }

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

    public int getToneSize() {
        return this.toneSize;
    }

    public boolean setToneSize(final int toneSize) {
        // TODO: 4/12/16 argument validation
        this.toneSize = toneSize;
        return true;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public boolean setSampleRate(final int sampleRate) {
        if(sampleRate == Configuration.SAMPLE_RATE_48KHZ ||
                sampleRate == Configuration.SAMPLE_RATE_44KHZ) {
            this.sampleRate = sampleRate;
            return true;
        } else {
            return false;
        }
    }

    public double getBaseFrequency() {
        return this.baseFrequency;
    }

    public boolean setBaseFrequency(final double baseFrequency) {
        // TODO: 4/12/16 argument validation
        this.baseFrequency = baseFrequency;
        return true;
    }

    public double getFrequencyDelta() {
        return this.frequencyDelta;
    }

    public boolean setFrequencyDelta(final double frequencyDelta) {
        // TODO: 4/12/16 arugment validation
        this.frequencyDelta = frequencyDelta;
        return true;
    }
}
