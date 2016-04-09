package ch.nych.soundtransmitter.receiver.tasks.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;

/**
 * Created by nych on 4/9/16.
 */
public class RecordingTask extends ReceiverTask {

    private SampleBuffer sampleBuffer = null;

    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int sampleRateInHz = 48000;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInShorts = 960;

    short buffer[] = new short[bufferSizeInShorts];
    AudioRecord audioRecorder = null;

    public RecordingTask(Receiver receiver) {
        super(receiver);
        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.audioRecorder = new AudioRecord(
                this.audioSource,
                this.sampleRateInHz,
                this.channelConfig,
                this.audioFormat,
                this.bufferSizeInShorts);
    }

    @Override
    public void run() {
        Log.i("MyTag", "Start Recording Task");
        if(this.audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            this.audioRecorder.startRecording();
        } else {
            //// TODO: 4/9/16
            return;
        }
        while(!this.shutdown) {
            this.audioRecorder.read(buffer, 0, bufferSizeInShorts);
            this.sampleBuffer.addSamples(this.buffer);
        }
        this.audioRecorder.stop();
        this.audioRecorder.release();
    }
}
