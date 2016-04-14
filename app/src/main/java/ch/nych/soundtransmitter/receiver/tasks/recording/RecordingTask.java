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

    private SampleBuffer sampleBuffer = null;

    short buffer[] = null;
    AudioRecord audioRecorder = null;

    public RecordingTask(Receiver receiver) {
        super(receiver);
    }

    public boolean initRecordingTask() {
        Log.d(this.logTag, "Initialize RecordingTask");
        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.buffer = new short[this.configuration.getAudioRecordBufferSize()];
        this.audioRecorder = new AudioRecord(
                this.configuration.getAudioSource(),
                this.configuration.getSampleRate(),
                this.configuration.getChannelConfig(),
                this.configuration.getAudioFormat(),
                this.configuration.getAudioRecordBufferSize());
        if(this.audioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(this.logTag, "Could not initialize AudioRecord object");
            return false;
        }
        return true;
    }

    private boolean startRecording() {
        if(this.audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            return false;
        }
        this.audioRecorder.startRecording();
        return true;
    }

    private void record() {
        while(!this.shutdown) {
            if(this.audioRecorder.read(this.buffer, 0, this.buffer.length) <= 0) {
                Log.e(this.logTag, "Error occured while reading from AudioRecord");
                return;
            } else {
                this.sampleBuffer.addSamples(this.buffer);
            }
        }
    }

    private void releaseAudioRecord() {
        if(this.audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            this.audioRecorder.release();
            if(this.audioRecorder.getRecordingState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.d(this.logTag, "Propperly released AudioRecord resource");
            } else {
                Log.e(this.logTag, "Could not release AudioRecord because its state was already" +
                        "AudioRecord.STATE_UNINITIALIZED");
            }
        }
    }

    @Override
    public void run() {
        Log.d(this.logTag, "Start Recording Task");
        if(!this.startRecording()) {
            Log.e(this.logTag, "Could not start recording because AudioRecord is not initialized");
        }
        this.record();
        this.releaseAudioRecord();
    }
}
