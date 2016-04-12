package ch.nych.soundtransmitter.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nych on 4/10/16.
 */
public class Config {

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
    public final static float ULTRASONIC_BASE_FREQUENCY = 19000.0f;

    /*
     *
     */
    public final static int LINEAR_TONE = 0;

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
    private float baseFrequency = 0.0f;


    private Config() {};

    public static Config newUltrasonicConfig() {
        Config config = new Config();
        config.transmissionMode = Config.FOUR_STATE_TRANSMISSION;
        config.toneType = Config.SINE_TONE;
        config.toneSize = Config.DEFAULT_TONE_SIZE;
        config.sampleRate = Config.SAMPLE_RATE_48KHZ;
        config.baseFrequency = Config.ULTRASONIC_BASE_FREQUENCY;

        return config;
    }

    public int getTransmissionMode() {
        return this.transmissionMode;
    }

    public int getToneType() {
        return this.toneType;
    }

    public int getToneSize() {
        return this.toneSize;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public float getBaseFrequency() {
        return this.baseFrequency;
    }
}
