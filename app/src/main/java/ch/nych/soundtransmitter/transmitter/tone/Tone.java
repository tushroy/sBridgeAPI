package ch.nych.soundtransmitter.transmitter.tone;

/**
 * Created by nych on 4/6/16.
 */
public interface Tone {
    public final static double DEFAULT_VOLUME = 1.0;
    public int getLength();
    public short[] getSamples();
}
