package ch.nych.soundtransmitter.transmitter.tasks.modulation.tone;

/**
 * Created by nych on 4/6/16.
 */
public interface Tone {
    public final static double DEFAULT_VOLUME = 1.0;

    public double getFrequency();
    public double getSampleRate();
    public double getLength();
    public double getVolume();
    public short[] getSamples();
}