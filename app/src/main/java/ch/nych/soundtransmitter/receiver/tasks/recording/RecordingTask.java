package ch.nych.soundtransmitter.receiver.tasks.recording;

import android.media.AudioRecord;
import android.util.Log;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/9/16.
 */
public class RecordingTask extends ReceiverTask {

    private final String logTag = Configuration.LOG_TAG;
    private SampleBuffer sampleBuffer = null;

    short buffer[] = null;
    AudioRecord audioRecorder = null;

    public RecordingTask(Receiver receiver) {
        super(receiver);
        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.buffer = new short[this.configuration.getAudioRecordBufferSize()];
        this.audioRecorder = new AudioRecord(
                this.configuration.getAudioSource(),
                this.configuration.getSampleRate(),
                this.configuration.getChannelConfig(),
                this.configuration.getAudioFormat(),
                this.configuration.getAudioRecordBufferSize());
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
            this.audioRecorder.read(this.buffer, 0, this.buffer.length);
            this.sampleBuffer.addSamples(this.buffer);
        }
        this.audioRecorder.stop();
        this.audioRecorder.release();
    }
}
