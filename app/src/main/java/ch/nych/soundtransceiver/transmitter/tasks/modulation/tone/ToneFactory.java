package ch.nych.soundtransceiver.transmitter.tasks.modulation.tone;

import android.util.Log;

import ch.nych.soundtransceiver.util.Configuration;

/**
 * This class is used for the initialization of a tone set by calling the static getToneSet()
 * method. This set can have different lengths as its possible to transmit a state with different
 * frequencies (see Configuration.transmissionMode). The first tone of the set is always the control
 * frequency.
 * This class can't be instantiated.
 * Created by nych on 4/12/16.
 */
public abstract class ToneFactory {

    /**
     * The local log tag
     */
    private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
            ":ToneFactory";

    /**
     * This method instantiates a set of tones.
     * @param configuration    the configuration instance of the calling m
     * @return an array of tones
     */
    public static Tone[] getToneSet(Configuration configuration) {
        Log.d(ToneFactory.LOG_TAG, "Generate ToneSet\n" +  "\tChannels:\t" +
                configuration.getTransmissionMode() + "channels\n" +
                "\tBaseFrequeny:\t" + configuration.getBaseFrequency() +
                "Hz\n" + "\tToneSize:\t\t" + configuration.getToneSize() + " " +
                "Samples\n" +  "\tFrequencyDelta:\t" + configuration
                .getFrequencyDelta() + "Hz\n" +  "\tSampleRate:\t\t" +
                configuration.getSampleRate() + "Hz");

        double[] frequencies = configuration.getFrequencies();
        Tone[] toneSet = new Tone[frequencies.length];
        if(configuration.getToneType() == Configuration.ToneType.SINE_TONE) {
            toneSet[0] = new SineTone(
                    frequencies[0],
                    configuration.getControlToneSize(),
                    configuration.getSampleRate().getSampleRate(),
                    configuration.getToneVolume());
            for (int i = 1; i < frequencies.length; i++) {
                toneSet[i] = new SineTone(
                        frequencies[i],
                        configuration.getToneSize(),
                        configuration.getSampleRate().getSampleRate(),
                        configuration.getToneVolume());
            }
        }
        return toneSet;
    }
}
