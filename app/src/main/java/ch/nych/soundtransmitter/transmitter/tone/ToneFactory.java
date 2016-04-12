package ch.nych.soundtransmitter.transmitter.tone;

import ch.nych.soundtransmitter.util.Config;

/**
 * Created by nych on 4/12/16.
 */
public class ToneFactory {

    private ToneFactory() {}

    public static Tone[] getToneSet(Config config) {
        int numberOfStates = config.getTransmissionMode() * 2 + 1;
        Tone[] toneSet = new Tone[numberOfStates];

        for(int i = 0; i < numberOfStates; i++) {
            double frequency = config.getBaseFrequency();
            frequency += i * config.getFrequencyDelta();
            if(config.getToneType() == Config.SINE_TONE) {
                toneSet[i] = new SineTone(
                        frequency,
                        config.getToneSize(),
                        config.getSampleRate(),
                        Tone.DEFAULT_VOLUME);
            }
        }
        return toneSet;
    }
}
