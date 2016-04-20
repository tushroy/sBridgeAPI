package ch.nych.soundtransmitter.transmitter.tone;

import org.junit.Test;

import ch.nych.soundtransmitter.util.Configuration;

import static org.junit.Assert.*;

/**
 * Created by nych on 4/20/16.
 */
public class ToneFactoryTest {

    @Test
    public void testGetToneSet() throws Exception {
        Configuration configuration = Configuration.newUltrasonicConfiguration();
        this.testRun(configuration);
        configuration.setTransmissionMode(Configuration.TWO_STATE_TRANSMISSION);
        this.testRun(configuration);
        configuration.setToneSize(960);
        this.testRun(configuration);
        configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        this.testRun(configuration);
    }

    private void testRun(final Configuration configuration) {
        Tone[] toneSet = ToneFactory.getToneSet(configuration);
        double frequency = configuration.getBaseFrequency();

        assertEquals(configuration.getTransmissionMode(), toneSet.length);
        for(int i = 0; i < toneSet.length; i++) {
            assertEquals(frequency, toneSet[i].getFrequency(), 0.001);
            assertEquals(configuration.getSampleRate(), toneSet[i].getSampleRate(), 0.001);
            assertEquals(configuration.getToneSize(), toneSet[i].getLength(), 0.001);
            assertEquals(configuration.getToneSize(), toneSet[i].getSamples().length);
            assertEquals(1, toneSet[i].getVolume(), 0.001);
            frequency += configuration.getFrequencyDelta();
        }
    }
}