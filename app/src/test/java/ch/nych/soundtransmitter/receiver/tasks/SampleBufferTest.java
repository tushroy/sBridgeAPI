package ch.nych.soundtransmitter.receiver.tasks;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.nych.soundtransmitter.util.Configuration;

import static org.junit.Assert.*;

/**
 * Created by nych on 4/20/16.
 */
public class SampleBufferTest {

    SampleBuffer sampleBuffer = null;
    Configuration configuration = null;

    @BeforeClass
    public void initTest() {
        this.configuration = Configuration.newUltrasonicConfiguration();
    }

    @Test
    public void testAddSamples() throws Exception {

    }

    @Test
    public void testGetNextWindow() throws Exception {

    }
}