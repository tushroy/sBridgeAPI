package ch.nych.soundtransmitter.transmitter.tone;

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
        int numberOfStates = configuration.getTransmissionMode() * 2 + 1;
        Tone[] toneSet = new Tone[numberOfStates];

        for(int i = 0; i < numberOfStates; i++) {
            double frequency = configuration.getBaseFrequency();
            frequency += i * configuration.getFrequencyDelta();
            if(configuration.getToneType() == Configuration.SINE_TONE) {
                toneSet[i] = new SineTone(
                        frequency,
                        configuration.getToneSize(),
                        configuration.getSampleRate(),
                        Tone.DEFAULT_VOLUME);
            }
        }
        return toneSet;
    }
}
