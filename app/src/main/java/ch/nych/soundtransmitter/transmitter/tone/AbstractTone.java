package ch.nych.soundtransmitter.transmitter.tone;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/6/16.
 */
public abstract class AbstractTone implements Tone {

    protected final String logTag = Configuration.LOG_TAG;

    // Tone frequency
    protected double frequency = 0;
    // Length of tone in samples
    protected double length = 0;
    // Sample rate of tone
    protected double sampleRate = 0;
    // Volume of tone
    protected double volume = 0;

    // The calculated samples
    protected short[] samples = null;

    public AbstractTone(double frequency, int length, int sampleRate, double volume) {
        this.frequency = frequency;
        this.sampleRate = sampleRate;
        this.length = length;
        this.volume = volume;
    }

    public int getLength() {
        return (int) this.length;
    }

    //to remove
    public double getFrequency() {
        return this.frequency;
    }
}
