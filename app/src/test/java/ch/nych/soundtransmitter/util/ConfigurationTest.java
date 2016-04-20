package ch.nych.soundtransmitter.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nych on 4/19/16.
 */
public class ConfigurationTest {

    private Configuration configuration = null;

    @Before
    public void setupTest() throws Exception {
        this.configuration = Configuration.newUltrasonicConfiguration();
    }

    @Test
    public void testSetTransmissionMode() throws Exception {
        assertEquals(true,
                this.configuration.setTransmissionMode(Configuration.FOUR_STATE_TRANSMISSION));
        assertEquals(true,
                this.configuration.setTransmissionMode(Configuration.TWO_STATE_TRANSMISSION));
        assertEquals(false,
                this.configuration.setTransmissionMode(0));
        assertEquals(false,
                this.configuration.setTransmissionMode(5));
    }

    @Test
    public void testGetTransmissionMode() throws Exception {
        assertEquals(4, this.configuration.getTransmissionMode());

        this.configuration.setTransmissionMode(Configuration.TWO_STATE_TRANSMISSION);
        assertEquals(2, this.configuration.getTransmissionMode());

        this.configuration.setTransmissionMode(Configuration.FOUR_STATE_TRANSMISSION);
        assertEquals(4, this.configuration.getTransmissionMode());
    }

    @Test
    public void testSetToneType() throws Exception {
        assertEquals(true, this.configuration.setToneType(Configuration.SINE_TONE));
        assertEquals(false, this.configuration.setToneType(0));
        assertEquals(false, this.configuration.setToneType(2));
    }

    @Test
    public void testGetToneType() throws Exception {
        assertEquals(1, this.configuration.getToneType());
    }

    @Test
    public void testSetToneSize() throws Exception {
        assertEquals(true, this.configuration.setToneSize(0));
        assertEquals(true, this.configuration.setToneSize(240));
        assertEquals(true, this.configuration.setToneSize(480));
        assertEquals(true, this.configuration.setToneSize(960));
    }


    @Test
    public void testGetToneSize() throws Exception {
        assertEquals(480, this.configuration.getToneSize());

        this.configuration.setToneSize(960);
        assertEquals(960, this.configuration.getToneSize());
    }

    @Test
    public void testSetSampleRate() throws Exception {
        assertEquals(true, this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ));
        assertEquals(true, this.configuration.setSampleRate(Configuration.SAMPLE_RATE_48KHZ));
        assertEquals(false, this.configuration.setSampleRate(43000));
        assertEquals(false, this.configuration.setSampleRate(49000));
    }

    @Test
    public void testGetSampleRate() throws Exception {
        assertEquals(44100, this.configuration.getSampleRate());

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_48KHZ);
        assertEquals(48000, this.configuration.getSampleRate());

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(44100, this.configuration.getSampleRate());
    }

    @Test
    public void testSetBaseFrequency() throws Exception {
        // TODO: 4/19/16 See todo at implementation in Configuration class
        /*assertEquals(true, this.configuration.setBaseFrequency(-1));
        assertEquals(true, this.configuration.setBaseFrequency(0));
        assertEquals(true, this.configuration.setBaseFrequency(18000));
        assertEquals(true, this.configuration.setBaseFrequency(23000));
        assertEquals(true, this.configuration.setBaseFrequency(23000));*/
    }

    @Test
    public void testGetBaseFrequency() throws Exception {
        assertEquals(19000, this.configuration.getBaseFrequency(), 0.0);
    }

    @Test
    public void testSetFrequencyDelta() throws Exception {
        // TODO: 4/19/16 See todo at implementation in Configuration class
    }

    @Test
    public void testGetFrequencyDelta() throws Exception {
       /* assertEquals(100, this.configuration.getFrequencyFactor(), 0.5);

        this.configuration.setWindowSize(960);
        assertEquals(50, this.configuration.getFrequencyFactor(), 0.5);

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(45, this.configuration.getFrequencyFactor(), 1.0);

        this.configuration.setSampleRate(480);
        assertEquals(91, this.configuration.getFrequencyFactor(), 1.0);*/
    }

    @Test
    public void testGetFrequencies() throws Exception {
        assertArrayEquals(new double[]{
                19000.0,
                19100.0,
                19200.0,
                19300.0,
                19400.0,
                19500.0,
                19600.0,
                19700.0,
                19800.0}, this.configuration.getFrequencies(), 1.0);

        /*this.configuration.setBaseFrequency(15000);
        assertArrayEquals(new double[]{
                15000.0,
                15100.0,
                15200.0,
                15300.0,
                15400.0,
                15500.0,
                15600.0,
                15700.0,
                15800.0}, this.configuration.getFrequencies(), 1.0);

        // TODO: 4/19/16 This test is not useless but the basefrequency should be a integer multiple
        // of 44100/480 in this case
        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertArrayEquals(new double[]{
                19000.0,
                19091.875,
                19183.75,
                19275.625,
                19367.5,
                19459.375,
                19551.25,
                19643.125,
                19735}, this.configuration.getFrequencies(), 1.0);*/
    }

    @Test
    public void testGetAudioSource() throws Exception {
        assertEquals(MediaRecorder.AudioSource.MIC, this.configuration.getAudioSource());
    }

    @Test
    public void testSetAudioSource() throws Exception {
        assertEquals(true, this.configuration.setAudioSource(Configuration.AUDIO_SOURCE));
        assertEquals(true, this.configuration.setAudioSource(MediaRecorder.AudioSource.MIC));
        assertEquals(false, this.configuration.setAudioSource(MediaRecorder.AudioSource.CAMCORDER));
    }

    @Test
    public void testGetChannelConfig() throws Exception {
        assertEquals(AudioFormat.CHANNEL_IN_MONO, this.configuration.getChannelConfig());
    }

    @Test
    public void testSetChannelConfig() throws Exception {
        assertEquals(true, this.configuration.setChannelConfig(Configuration.CHANNEL_CONFIG));
        assertEquals(true, this.configuration.setChannelConfig(AudioFormat.CHANNEL_IN_MONO));
        assertEquals(false, this.configuration.setChannelConfig(AudioFormat.CHANNEL_IN_BACK));
    }

    @Test
    public void testGetAudioFormat() throws Exception {
        assertEquals(AudioFormat.ENCODING_PCM_16BIT, this.configuration.getAudioFormat());
    }

    @Test
    public void testSetAudioFormat() throws Exception {
        assertEquals(true, this.configuration.setAudioFormat(Configuration.AUDIO_FORMAT));
        assertEquals(true, this.configuration.setAudioFormat(AudioFormat.ENCODING_PCM_16BIT));
        assertEquals(false, this.configuration.setAudioFormat(AudioFormat.ENCODING_PCM_8BIT));

    }

    @Test
    public void testGetAudioRecordBufferSize() throws Exception {
        assertEquals(AudioRecord.getMinBufferSize(
                configuration.getSampleRate(),
                configuration.getChannelConfig(),
                configuration.getAudioFormat() / 2), this.configuration.getAudioRecordBufferSize());
    }

    @Test
    public void testGetMinimumAudioRecordBufferSize() throws Exception {

    }

    @Test
    public void testSetAudioRecordBufferSize() throws Exception {
        assertEquals(true, this.configuration.setAudioRecordBufferSize(
                AudioRecord.getMinBufferSize(
                        configuration.getSampleRate(),
                        configuration.getChannelConfig(),
                        configuration.getAudioFormat())));

        assertEquals(false, this.configuration.setAudioRecordBufferSize(960));
    }

    @Test
    public void testGetSampleBufferSize() throws Exception {
    }

    @Test
    public void testSetSampleBufferSize() throws Exception {

    }

    @Test
    public void testGetWindowSize() throws Exception {

    }

    @Test
    public void testSetWindowSize() throws Exception {

    }

    @Test
    public void testGetOverlappingFactor() throws Exception {

    }

    @Test
    public void testSetOverlappingFactor() throws Exception {

    }

    @Test
    public void testGetWindowFunction() throws Exception {

    }

    @Test
    public void testSetWindowFunction() throws Exception {

    }

    @Test
    public void testNewUltrasonicConfiguration() throws Exception {

    }
}