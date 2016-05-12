package ch.nych.soundtransceiver.transmitter.tasks.modulation.tone;

import android.util.Log;

import ch.nych.soundtransceiver.util.Configuration;

/**
 * Because it should possible to transmit the data inaudible, the transition
 * between the single tones needs to be faded. SineTone uses a sine function
 * for the fade in and fade out.
 * Created by nych on 4/6/16.
 */
public class SineTone extends Tone {

    /**
     * Local log tag
     */
    private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
            ":SineTone";

    /**
     * Default constructor
     * @param frequency     tone frequency in hertz
     * @param length        tone length in samples
     * @param sampleRate    sample rate in hertz
     * @param volume        volume in percent
     */
    public SineTone(final double frequency,
                final int length,
                final int sampleRate,
                final double volume) {
        super(frequency, length, sampleRate, volume);
        Log.d(SineTone.LOG_TAG, "Create new SineTone Object\n" +
                "\tFrequency:\t\t" + frequency);
    }

    /**
     * This method calculates the single sample values of the tone. The
     * frequency of the sine function, used for the fade in and fade  out, is
     * calculated by the division of tone length and the sample rate.
     */
    private void generateTone() {
        Log.d(SineTone.LOG_TAG, "Generate Tone Samples for SineTone of: " +
                this.frequency + "Hz");
        this.samples = new short[(int) this.length];
        short volume = (short) (Short.MAX_VALUE * this.volume);
        double const1 = 2 * Math.PI * (1.0 / (this.length * 2));
        double const2 = 2 * Math.PI * (this.frequency / this.sampleRate);

        for(int i = 0; i < this.length; i++)
            this.samples[i] = (short) (Math.sin(i * const1) *
                    Math.sin(i * const2) * volume);
    }

    @Override
    public short[] getSamples() {
        if(this.samples == null) {
            this.generateTone();
        }
        return this.samples;
    }
}
