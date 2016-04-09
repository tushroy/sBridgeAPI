package ch.nych.soundtransmitter.receiver.tasks.transformation;

/**
 * The Goertzel class can be used to perform the Goertzel algorithm. In
 * order to use this class, four primary steps should be executed:
 * initialize the Goertzel class and all its variables (initGoertzel),
 * process one sample of data at a time (processSample),
 * get the relative magnitude returned by the Goertzle algorithm after N
 * samples have been processed (getMagnitudeSquared, getRealImag),
 * and reset the Goertzel class and all its variables (resetGoertzel).
 * <p>
 * This class is based on a C program implemented by Kevin Banks of Embedded
 * Systems Programming.
 * <p>
 * Brought it to the object oriented world.
 *
 * @author quantasy
 * @author Chris Palistrant, Tony Offer
 * @version 0.0, May 2004
 */
public class Goertzel {

    private double sampleRate;
    private double targetFrequency;
    private int n;

    private double coeff;
    private double Q1;
    private double Q2;
    private double sine;
    private double cosine;

    private double magnitudeSquared;

    /**
     * Constructor
     *
     * @param sampleRate
     *                      is the sampling rate of the signal to be analyzed
     * @param targetFrequency
     *                      is the frequency that Goertzel will look for.
     * @param inN
     *                      is the block size to use with Goertzel
     */
    public Goertzel(final double sampleRate, final double targetFrequency, final int inN){
        this.sampleRate = sampleRate;
        this.targetFrequency = targetFrequency;
        this.n = inN;
        this.initGoertzel();
    }

    public void initGoertzel() {
        int k;
        double omega;

        k = (int) (0.5 + ((this.n * this.targetFrequency) / this.sampleRate));
        omega = (2.0 * Math.PI * k) / this.n;
        this.sine = Math.sin(omega);
        this.cosine = Math.cos(omega);
        this.coeff = 2.0 * cosine;

        this.resetGoertzel();
    }

    public void resetGoertzel() {
        this.Q2 = 0;
        this.Q1 = 0;
        this.magnitudeSquared = -1;
    }

    public void processSample(double sample) {
        double Q0;

        Q0 = this.coeff * this.Q1;
        Q0 -= this.Q2;
        Q0 += sample;
        this.Q2 = this.Q1;
        this.Q1 = Q0;
    }

    public double[] getRealImag() {
        double real = this.Q1 - this.Q2 * this.cosine;
        double imag = this.Q2 * this.sine;
        return new double[]{real, imag};
    }

    public double getMagnitudeSquared() {
        if (this.magnitudeSquared < 0) {
            this.magnitudeSquared = this.Q1 * this.Q1;
            this.magnitudeSquared += this.Q2 * this.Q2;
            this.magnitudeSquared -= this.Q1 * this.Q2 * this.coeff;
        }
        return this.magnitudeSquared;
    }

    public double getTargetFrequency() {
        return this.targetFrequency;
    }
}
