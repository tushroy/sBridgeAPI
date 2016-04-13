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
    public final static double DEFAULT_FREQUENCY_DELTA = 100.0;

    /*
     *
     */
    public final static int SINE_TONE = 1;

    /* -------------------------------------------------------------------------------------------*/
    /*                                         AudioRecord                                        */

    /**
     *
     */
    private final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    /**
     *
     */
    private final static int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    /**
     *
     */
    private final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /* -------------------------------------------------------------------------------------------*/


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
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    private double baseFrequency = 0.0;

    public double getBaseFrequency() {
        return this.baseFrequency;
    }

    public boolean setBaseFrequency(final double baseFrequency) {
        // TODO: 4/12/16 argument validation
        this.baseFrequency = baseFrequency;
        return true;
    }

    /**
     *
     */
    private double frequencyDelta = 0.0;

    public double getFrequencyDelta() {
        return this.frequencyDelta;
    }

    public boolean setFrequencyDelta(final double frequencyDelta) {
        // TODO: 4/12/16 arugment validation
        this.frequencyDelta = frequencyDelta;
        return true;
    }

    /**
     *
     */
    private int audioSource = 0;

    public int getAudioSource() {
        return this.audioSource;
    }

    public boolean setAudioSource(final int audioSource) {
        //// TODO: 4/13/16 Validate if there are other possible audio source for our purpose
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
        //// TODO: 4/13/16 Validate if there are other possible channel configurations for our purpose
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



    private Configuration() {};

    public static Configuration newUltrasonicConfiguration() {
        Configuration configuration = new Configuration();
        configuration.transmissionMode = Configuration.FOUR_STATE_TRANSMISSION;
        configuration.toneType = Configuration.SINE_TONE;
        configuration.toneSize = Configuration.DEFAULT_TONE_SIZE;
        configuration.sampleRate = Configuration.SAMPLE_RATE_48KHZ;
        configuration.baseFrequency = Configuration.ULTRASONIC_BASE_FREQUENCY;
        configuration.frequencyDelta = Configuration.DEFAULT_FREQUENCY_DELTA;
        configuration.audioSource = Configuration.AUDIO_SOURCE;
        configuration.channelConfig = Configuration.CHANNEL_CONFIG;
        configuration.audioFormat = Configuration.AUDIO_FORMAT;
        configuration.audioRecordBufferSize = configuration.getMinimumAudioRecordBufferSize();
        configuration.sampleBufferSize = configuration.getAudioRecordBufferSize() * 10;
        return configuration;
    }
}
