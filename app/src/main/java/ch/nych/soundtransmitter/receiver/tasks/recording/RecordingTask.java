package ch.nych.soundtransmitter.receiver.tasks.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.util.Configuration;

/**
 *  This class implements the audio recording task. The recorded samples are buffered locally and
 *  is the stored to the shared {@link SampleBuffer} instance. Before the recording can happen, the
 *  audio resources need to be initialized. Therefore the class implements the initTask() method,
 *  inherited from {@link ReceiverTask}. Forgetting the initialization will result in an error based
 *  shutdown. Once the startRecording method is called successfully, it keeps recording until the
 *  shutdown() method is called. This can either happen manually because the recording is not longer
 *  wanted, or through an error in the native library.
 */
public class RecordingTask extends ReceiverTask {

    /**
     *
     */
    private final String logTag = Configuration.LOG_TAG + "recTask";

    /**
     * Local reference to the shared sampleBuffer object
     */
    private SampleBuffer sampleBuffer = null;

    /**
     * Local array to buffer the recorded samples
     */
    short buffer[] = null;

    /**
     * Reference to the shared AudioRecord instance
     */
    AudioRecord audioRecorder = null;

    /**
     *  The default constructor for the recording task.
     * @param receiver The receiver reference for the shared resources
     */
    public RecordingTask(final Receiver receiver) {
        super(receiver);
    }

    @Override
    public boolean initTask() {
        Log.d(this.logTag, "Initialize RecordingTask");

        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.buffer = new short[AudioRecord.getMinBufferSize(
                this.configuration.getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT) / 2];
        this.audioRecorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                this.configuration.getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                this.buffer.length);

        if(this.audioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(this.logTag, "Could not initialize AudioRecord object");
            return false;
        }
        return true;
    }

    /**
     * This method tries to start the recording of the {@link AudioRecord} instance. If the instance
     * is not initialized, an exceptions is thrown by {@link AudioRecord} and the
     * {@link RecordingTask} is shutting down.
     */
    private void startRecording() {
        Log.d(this.logTag, "Start Recording Task");

        try {
            this.audioRecorder.startRecording();
        } catch (IllegalStateException e) {
            Log.e(this.logTag, e.getMessage() + "\t Terminate recording task.");
            this.shutdown();
        }
    }

    /**
     * This method keeps recording until the shutdown flag is true or {@link AudioRecord} returns
     * an error code. Both ways result in a shutdown of the {@link RecordingTask}.
     */
    private void record() {
        int samplesRecorded = 0;
        while(!this.shutdown) {
            if((samplesRecorded =
                    this.audioRecorder.read(this.buffer, 0, this.buffer.length)) <= 0) {
                Log.e(this.logTag, "Error occured while reading from AudioRecord. AudioRecord" +
                        "error code is " + samplesRecorded + "\nTerminate recording task.");
                this.shutdown();
            } else {
                //Log.d(this.logTag, "Recorded " + samplesRecorded);
                this.sampleBuffer.addSamples(this.buffer);
            }
        }
    }

    /**
     * AudioRecord holds native resources that need to be release. When the resources are release,
     * the reference is set to null.
     */
    private void releaseAudioRecord() {
        Log.d(this.logTag, "Release native AudioRecord resources.");

        this.audioRecorder.release();
        this.audioRecorder = null;
    }

    @Override
    public void run() {
        this.startRecording();
        this.record();
        this.releaseAudioRecord();
    }
}
