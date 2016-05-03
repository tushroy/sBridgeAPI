package ch.nych.soundtransmitter.transmitter.tasks.modulation.tone;

import android.util.Log;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/12/16.
 */
public class ToneFactory {

    private static final String logTag = Configuration.LOG_TAG;

    private ToneFactory() {}

    public static Tone[] getToneSet(Configuration configuration) {
        Log.d(ToneFactory.logTag,
                "Generate ToneSet\n" +
                        "\tStateMachine:\t" + configuration.getTransmissionMode() + "States\n" +
                        "\tBaseFrequeny:\t" + configuration.getBaseFrequency() + "Hz\n" +
                        "\tToneSize:\t\t" + configuration.getToneSize() + " Samples\n" +
                        "\tFrequencyDelta:\t" + configuration.getFrequencyDelta() + "Hz\n" +
                        "\tSampleRate:\t\t" + configuration.getSampleRate() + "Hz");

        double[] frequencies = configuration.getFrequencies();
        Tone[] toneSet = new Tone[frequencies.length];
        toneSet[0] = new SineTone(
                frequencies[0],
                // TODO: 5/3/16 this is dangerous
                configuration.getToneSize() * 2,
                configuration.getSampleRate(),
                Tone.DEFAULT_VOLUME);
        for(int i = 1; i < frequencies.length; i++) {
            toneSet[i] = new SineTone(
                    frequencies[i],
                    configuration.getToneSize(),
                    configuration.getSampleRate(),
                    Tone.DEFAULT_VOLUME);
        }
        return toneSet;
    }
}
