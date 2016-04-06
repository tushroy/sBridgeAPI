package ch.nych.soundtransmitter.transmitter.tone;

/**
 * Created by nych on 4/6/16.
 */
public interface Tone {
    public final static int TONE_TYPE_SINE = 0;
    public final static int TONE_TYPE_LIN = 1;

    public int getLength();

   /* public int getFrequency();
    public int getSampleRate();

    public double getVolume();
    public void setFrequency(int frequency);
    public void setSampleRate(int sampleRate);
    public void setLength(int length);
    public void setVolume(double volume);*/
    public short[] getSamples();
}
