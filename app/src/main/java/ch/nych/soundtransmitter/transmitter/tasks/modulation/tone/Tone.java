package ch.nych.soundtransmitter.transmitter.tasks.modulation.tone;


/**
 * This is the abstract super class for all possible forms of a tone. The samples of a tone are used
 * for the data modulation.
 * Created by nych on 4/6/16.
 */
public abstract class Tone {

    /**
     *
     */
    public final static double DEFAULT_VOLUME = 1.0;

    /**
     * Frequency of the tone
     */
    protected double frequency = 0;

    /**
     * Sample rate of tone
     */
    protected double sampleRate = 0;

    /**
     * Length of tone in samples
     */
    protected double length = 0;

    /**
     * Volume of tone in percent
     */
    protected double volume = 0;

    /**
     * The calculated samples
     */
    protected short[] samples = null;

    /**
     * Default constructor
     * @param frequency     tone frequency in hertz
     * @param length        tone length in samples
     * @param sampleRate    sample rate in hertz
     * @param volume        volume in percent
     */
    public Tone(final double frequency,
                final int length,
                final int sampleRate,
                final double volume) {
        this.frequency = frequency;
        this.sampleRate = sampleRate;
        this.length = length;
        this.volume = volume;
    }

    /**
     * This method returns the calculated samples of the specified tone.
     * @return the sample values in short array
     */
    public abstract short[] getSamples();

    /**
     * Getter for the tone frequency
     * @return the tone frequency
     */
    public double getFrequency() {
        return this.frequency;
    }

    /**
     * Getter for the tone length
     * @return the tone length
     */
    public double getLength() {
        return this.length;
    }
}
