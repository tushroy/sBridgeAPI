package ch.nych.soundtransmitter.transmitter.tone;

/**
 * Created by nych on 4/6/16.
 */
public class SineTone extends AbstractTone {

    public SineTone(int frequency, int length, int sampleRate, double volume) {
        super(frequency, length, sampleRate, volume);
    }

    @Override
    public short[] getSamples() {
        if(this.samples == null) {
            this.generateTone();
        }
        return this.samples;
    }

    /**
     * Generates a tone of frequency f with a fade sine in/out of frequency
     * length / samplerate
     */
    private void generateTone() {
        this.samples = new short[(int) this.length];
        short volume = (short) (Short.MAX_VALUE * this.volume);
        double const1 = 2 * Math.PI * (1.0 / (this.length * 2));
        double const2 = 2 * Math.PI * (this.frequency / this.sampleRate);

        for(int i = 0; i < this.length; i++)
            this.samples[i] = (short) (Math.sin(i * const1) * Math.sin(i * const2) * volume);
    }
}
