package ch.nych.soundtransmitter.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.junit.Before;
import org.junit.Test;

import java.security.cert.CertificateParsingException;
import java.security.spec.ECField;

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
                this.configuration.setTransmissionMode(10));
    }

    @Test
    public void testGetTransmissionMode() throws Exception {
        assertEquals(9, this.configuration.getTransmissionMode());

        this.configuration.setTransmissionMode(Configuration.TWO_STATE_TRANSMISSION);
        assertEquals(5, this.configuration.getTransmissionMode());

        this.configuration.setTransmissionMode(Configuration.FOUR_STATE_TRANSMISSION);
        assertEquals(9, this.configuration.getTransmissionMode());
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
        assertEquals(true, this.configuration.setToneSize(Configuration.MIN_TONE_SIZE));
        assertEquals(true, this.configuration.setToneSize(Configuration.MIN_TONE_SIZE + 1));
        assertEquals(true, this.configuration.setToneSize(Configuration.MAX_TONE_SIZE - 1));
        assertEquals(false, this.configuration.setToneSize(Configuration.MIN_TONE_SIZE - 1));
        assertEquals(false, this.configuration.setToneSize(Configuration.MAX_TONE_SIZE + 1));
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
        assertEquals(48000, this.configuration.getSampleRate());

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(44100, this.configuration.getSampleRate());

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_48KHZ);
        assertEquals(48000, this.configuration.getSampleRate());
    }

    @Test
    public void testGetNyquistFrequency() throws Exception {
        assertEquals(24000, this.configuration.getNyquistFrequency(), 0.001);
        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(22050, this.configuration.getNyquistFrequency(), 0.001);
    }

    @Test
    public void testSetWindowSize() throws Exception {
        assertEquals(false, this.configuration.setWindowSize(Configuration.MIN_WINDOW_SIZE - 1));
        assertEquals(false, this.configuration.setWindowSize(Configuration.SAMPLE_RATE_48KHZ + 1));

        assertEquals(true, this.configuration.setWindowSize(Configuration.MIN_WINDOW_SIZE));
        assertEquals(true, this.configuration.setWindowSize(Configuration.SAMPLE_RATE_48KHZ));

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(false, this.configuration.setWindowSize(Configuration.SAMPLE_RATE_48KHZ));
        assertEquals(true, this.configuration.setWindowSize(Configuration.SAMPLE_RATE_44KHZ));

    }

    @Test
    public void testGetWindowSize() throws Exception {
        assertEquals(480, this.configuration.getWindowSize());

        this.configuration.setWindowSize(512);
        assertEquals(512, this.configuration.getWindowSize());
    }

    @Test
    public void testGetFrequencyResolution() throws Exception {
        assertEquals(100, this.configuration.getFrequencyResolution(), 0.001);

        this.configuration.setWindowSize(960);
        assertEquals(50, this.configuration.getFrequencyResolution(), 0.001);

        this.configuration.setWindowSize(this.configuration.getSampleRate());
        assertEquals(1, this.configuration.getFrequencyResolution(), 0.001);


        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        this.configuration.setWindowSize(Configuration.MIN_WINDOW_SIZE);
        assertEquals(91.875, this.configuration.getFrequencyResolution(), 0.001);

        this.configuration.setWindowSize(960);
        assertEquals(45.9375, this.configuration.getFrequencyResolution(), 0.001);

        this.configuration.setWindowSize(this.configuration.getSampleRate());
        assertEquals(1, this.configuration.getFrequencyResolution(), 0.001);
    }

    @Test
    public void testSetFrequencyResolutionFactor() throws Exception {
        assertEquals(false, this.configuration.setFrequencyResolutionFactor(0));
        assertEquals(false, this.configuration.setFrequencyResolutionFactor(6));
        assertEquals(true, this.configuration.setFrequencyResolutionFactor(5));
        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(false, this.configuration.setFrequencyResolutionFactor(4));
        assertEquals(true, this.configuration.setFrequencyResolutionFactor(3));
        this.configuration.setWindowSize(Configuration.MIN_WINDOW_SIZE * 2);
        assertEquals(false, this.configuration.setFrequencyResolutionFactor(8));
        assertEquals(true, this.configuration.setFrequencyResolutionFactor(7));
        this.configuration.setTransmissionMode(Configuration.TWO_STATE_TRANSMISSION);
        assertEquals(false, this.configuration.setFrequencyResolutionFactor(14));
        assertEquals(true, this.configuration.setFrequencyResolutionFactor(13));
        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_48KHZ);
        assertEquals(false, this.configuration.setFrequencyResolutionFactor(21));
        assertEquals(true, this.configuration.setFrequencyResolutionFactor(20));
    }

    @Test
    public void testGetFrequencyResolutionFactor() throws Exception {
        assertEquals(1, this.configuration.getFrequencyResolutionFactor(), 0.001);

        this.configuration.setFrequencyResolutionFactor(2);
        assertEquals(2, this.configuration.getFrequencyResolutionFactor(), 0.001);
    }

    @Test
    public void testGetFrequencyDelta() throws Exception {
        assertEquals(100, this.configuration.getFrequencyDelta(), 0.001);

        this.configuration.setFrequencyResolutionFactor(2);
        assertEquals(200, this.configuration.getFrequencyDelta(), 0.001);

        this.configuration.setWindowSize(960);
        assertEquals(100, this.configuration.getFrequencyDelta(), 0.001);

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(91.875, this.configuration.getFrequencyDelta(), 0.001);

        this.configuration.setFrequencyResolutionFactor(1);
        assertEquals(45.9375, this.configuration.getFrequencyDelta(), 0.001);

    }


    @Test
    public void testCalcBaseFrequency() throws Exception {
        assertEquals(-1, this.configuration.calcBaseFrequency(0), 0.001);
        assertEquals(19000, this.configuration.calcBaseFrequency(19000), 0.001);

        this.configuration.setWindowSize(960);
        assertEquals(19000, this.configuration.calcBaseFrequency(19000), 0.001);

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        assertEquals(18972.1875, this.configuration.calcBaseFrequency(19000), 0.001);

        this.configuration.setWindowSize(480);
        assertEquals(18926.25, this.configuration.calcBaseFrequency(19000), 0.001);
    }

    @Test
    public void testGetBaseFrequency() throws Exception {
        assertEquals(19000, this.configuration.getBaseFrequency(), 0.0);
    }

    @Test
    public void testSetBaseFrequency() throws Exception {
        double nyquistLimit = this.configuration.getNyquistFrequency() -
                (this.configuration.getTransmissionMode() * this.configuration.getFrequencyDelta());

        assertEquals(false, this.configuration.setBaseFrequency(0, true));
        assertEquals(false, this.configuration.setBaseFrequency(0, false));
        assertEquals(false, this.configuration.setBaseFrequency(nyquistLimit + 1, true));
        assertEquals(false, this.configuration.setBaseFrequency(nyquistLimit + 1, false));
        assertEquals(true, this.configuration.setBaseFrequency(nyquistLimit, true));
        assertEquals(true, this.configuration.setBaseFrequency(nyquistLimit, false));

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);
        nyquistLimit = this.configuration.getNyquistFrequency() -
                (this.configuration.getTransmissionMode() * this.configuration.getFrequencyDelta());
        assertEquals(false, this.configuration.setBaseFrequency(0, true));
        assertEquals(false, this.configuration.setBaseFrequency(0, false));
        assertEquals(false, this.configuration.setBaseFrequency(nyquistLimit + 1, true));
        assertEquals(false, this.configuration.setBaseFrequency(nyquistLimit + 1, false));
        assertEquals(true, this.configuration.setBaseFrequency(nyquistLimit, true));
        assertEquals(true, this.configuration.setBaseFrequency(nyquistLimit, false));

        this.configuration.setBaseFrequency(19000, true);
        assertEquals(18926.25, this.configuration.getBaseFrequency(), 0.001);
        this.configuration.setBaseFrequency(19000, false);
        assertEquals(19000, this.configuration.getBaseFrequency(), 0.001);

        this.configuration.setWindowSize(Configuration.MIN_WINDOW_SIZE * 2);
        this.configuration.setBaseFrequency(19000, true);
        assertEquals(18972.1875, this.configuration.getBaseFrequency(), 0.001);
        this.configuration.setBaseFrequency(19000, false);
        assertEquals(19000, this.configuration.getBaseFrequency(), 0.001);
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
                19800.0}, this.configuration.getFrequencies(), 0.001);

        this.configuration.setWindowSize(960);

        assertArrayEquals(new double[]{
                19000.0,
                19050.0,
                19100.0,
                19150.0,
                19200.0,
                19250.0,
                19300.0,
                19350.0,
                19400.0}, this.configuration.getFrequencies(), 0.001);

        this.configuration.setSampleRate(Configuration.SAMPLE_RATE_44KHZ);

        assertArrayEquals(new double[]{
                19000.0,
                19045.93750,
                19091.875,
                19137.81250,
                19183.75000,
                19229.68750,
                19275.62500,
                19321.56250,
                19367.50000}, this.configuration.getFrequencies(), 0.001);

        this.configuration.setWindowSize(480);

        assertArrayEquals(new double[]{
                19000.0,
                19091.875,
                19183.75,
                19275.625,
                19367.5,
                19459.375,
                19551.25,
                19643.125,
                19735}, this.configuration.getFrequencies(), 0.001);

        this.configuration.setTransmissionMode(Configuration.TWO_STATE_TRANSMISSION);

        assertArrayEquals(new double[]{
                19000.0,
                19091.875,
                19183.75,
                19275.625,
                19367.5}, this.configuration.getFrequencies(), 0.001);
    }

    @Test
    public void testGetAudioSource() throws Exception {
    //    assertEquals(MediaRecorder.AudioSource.MIC, this.configuration.getAudioSource());
    }

    @Test
    public void testSetAudioSource() throws Exception {
     /*   assertEquals(true, this.configuration.setAudioSource(Configuration.AUDIO_SOURCE));
        assertEquals(true, this.configuration.setAudioSource(MediaRecorder.AudioSource.MIC));
        assertEquals(false, this.configuration.setAudioSource(MediaRecorder.AudioSource.CAMCORDER));*/
    }

    @Test
    public void testGetChannelConfig() throws Exception {
       // assertEquals(AudioFormat.CHANNEL_IN_MONO, this.configuration.getChannelConfig());
    }

    @Test
    public void testSetChannelConfig() throws Exception {
       /* assertEquals(true, this.configuration.setChannelConfig(Configuration.CHANNEL_CONFIG));
        assertEquals(true, this.configuration.setChannelConfig(AudioFormat.CHANNEL_IN_MONO));
        assertEquals(false, this.configuration.setChannelConfig(AudioFormat.CHANNEL_IN_BACK));*/
    }

    @Test
    public void testGetAudioFormat() throws Exception {
     //   assertEquals(AudioFormat.ENCODING_PCM_16BIT, this.configuration.getAudioFormat());
    }

    @Test
    public void testSetAudioFormat() throws Exception {
       /* assertEquals(true, this.configuration.setAudioFormat(Configuration.AUDIO_FORMAT));
        assertEquals(true, this.configuration.setAudioFormat(AudioFormat.ENCODING_PCM_16BIT));
        assertEquals(false, this.configuration.setAudioFormat(AudioFormat.ENCODING_PCM_8BIT));*/

    }

    @Test
    public void testGetAudioRecordBufferSize() throws Exception {
        /*assertEquals(AudioRecord.getMinBufferSize(
                configuration.getSampleRate(),
                configuration.getChannelConfig(),
                configuration.getAudioFormat() / 2), this.configuration.getAudioRecordBufferSize());*/
    }

    @Test
    public void testGetMinimumAudioRecordBufferSize() throws Exception {

    }

    @Test
    public void testSetAudioRecordBufferSize() throws Exception {

    }

    @Test
    public void testGetSampleBufferSize() throws Exception {
    }

    @Test
    public void testSetSampleBufferSize() throws Exception {

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