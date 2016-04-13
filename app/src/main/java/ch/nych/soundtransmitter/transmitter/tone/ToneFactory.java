package ch.nych.soundtransmitter.transmitter.tone;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/12/16.
 */
public class ToneFactory {

    private ToneFactory() {}

    public static Tone[] getToneSet(Configuration configuration) {
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
