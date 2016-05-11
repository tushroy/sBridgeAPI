package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.util.Log;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * The Goertzel class can be used to perform the Goertzel algorithm. In order to use this class,
 * four primary steps should be executed:
 * initialize the Goertzel class and all its variables (initGoertzel),
 * process one sample of data at a time (processSample),
 * get the relative magnitude returned by the Goertzle algorithm after N
 * samples have been processed (getMagnitudeSquared, getRealImag),
 * and reset the Goertzel class and all its variables (resetGoertzel).
 *
 * This class is based on a C program implemented by Kevin Banks of Embedded Systems Programming.
 *
 * Brought it to the object oriented world.
 *
 * @author quantasy
 * @author Chris Palistrant, Tony Offer
 * @version 0.0, May 2004
 */
public class Goertzel {

    private final static String logTag = Configuration.LOG_TAG + ":Goertzel";
    /**
     *
     */
    private double sampleRate;

    /**
     *
     */
    private double targetFrequency;

    /**
     *
     */
    private int windowSize;

    /**
     *
     */
    private double coeff;

    /**
     *
     */
    private double q1;

    /**
     *
     */
    private double q2;

    /**
     *
     */
    private double sine;

    /**
     *
     */
    private double cosine;

    /**
     *
     */
    private double magnitudeSquared;

    /**
     * Constructor
     *
     * @param sampleRate
     *                      is the sampling rate of the signal to be analyzed
     * @param targetFrequency
     *                      is the frequency that Goertzel will look for.
     * @param windowSize
     *                      is the window size to use with Goertzel
     */
    public Goertzel(final double sampleRate, final double targetFrequency, final int windowSize){
        this.sampleRate = sampleRate;
        this.targetFrequency = targetFrequency;
        this.windowSize = windowSize;
        this.initGoertzel();
    }

    /**
     *
     */
    public void initGoertzel() {
        int k;
        double omega;

        k = (int) (0.5 + ((this.windowSize * this.targetFrequency) / this.sampleRate));
        omega = (2.0 * Math.PI * k) / this.windowSize;
        this.sine = Math.sin(omega);
        this.cosine = Math.cos(omega);
        this.coeff = 2.0 * cosine;

        this.resetGoertzel();
    }

    /**
     *
     */
    public void resetGoertzel() {
        this.q2 = 0;
        this.q1 = 0;
        this.magnitudeSquared = -1;
    }

    /**
     *
     * @param sample
     */
    public void processSample(double sample) {
        double Q0;

        Q0 = this.coeff * this.q1;
        Q0 -= this.q2;
        Q0 += sample;
        this.q2 = this.q1;
        this.q1 = Q0;
    }

    /**
     *
     * @param samples
     */
    public void processSamples(final short[] samples) {
        if(samples == null) {
            Log.w(this.logTag, "called processSamples with null value");
            return;
        }
        for(int i = 0; i < samples.length; i++) {
            this.processSample(samples[i]);
        }
    }

    /**
     *
     * @return
     */
    public double[] getRealImag() {
        double real = this.q1 - this.q2 * this.cosine;
        double imag = this.q2 * this.sine;
        return new double[]{real, imag};
    }

    /**
     *
     * @return
     */
    public double getMagnitudeSquared() {
        if (this.magnitudeSquared < 0) {
            this.magnitudeSquared = this.q1 * this.q1;
            this.magnitudeSquared += this.q2 * this.q2;
            this.magnitudeSquared -= this.q1 * this.q2 * this.coeff;
        }
        return this.magnitudeSquared;
    }

    /**
     *
     * @return
     */
    public double getTargetFrequency() {
        return this.targetFrequency;
    }
}
