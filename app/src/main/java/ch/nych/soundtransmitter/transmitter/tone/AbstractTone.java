package ch.nych.soundtransmitter.transmitter.tone;

/**
 * Created by nych on 4/6/16.
 */
public abstract class AbstractTone implements Tone {
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

    public AbstractTone(int frequency, int length, int sampleRate, double volume) {
        this.frequency = frequency;
        this.sampleRate = sampleRate;
        this.length = length;
        this.volume = volume;
    }

    public int getLength() {
        return (int) this.length;
    }
}