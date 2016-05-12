package ch.nych.soundtransceiver.receiver.tasks.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.receiver.tasks.ReceiverTask;
import ch.nych.soundtransceiver.receiver.tasks.SampleBuffer;
import ch.nych.soundtransceiver.util.Configuration;

/**
 *  This class implements the audio recording task. The recorded samples are
 *  buffered locally and is the stored to the shared {@link SampleBuffer}
 *  instance. Before the recording can happen, the audio resources need to be
 *  initialized. Therefore the class implements the initTask() method,
 *  inherited from {@link ReceiverTask}. Forgetting the initialization will
 *  result in an error based shutdown. Once the startRecording method is
 *  called successfully, it keeps recording until the shutdown() method is
 *  called. This can either happen manually because the recording is not longer
 *  wanted, or through an error in the native library.
 */
public class RecordingTask extends ReceiverTask {

    /**
     *
     */
    private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
            ":recordingTask";

    /**
     * Local reference to the shared sampleBuffer object
     */
    private SampleBuffer sampleBuffer = null;

    /**
     * Local array to buffer the recorded samples
     */
    private short buffer[] = null;

    /**
     *
     */
    private AudioRecord audioRecord = null;

    /**
     *  The default constructor for the recording task.
     * @param receiver The receiver reference for the shared resources
     */
    public RecordingTask(final Receiver receiver) {
        super(receiver);
    }

    @Override
    public boolean initTask() {
        Log.d(RecordingTask.LOG_TAG, "Initialize RecordingTask");

        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.buffer = new short[AudioRecord.getMinBufferSize(
                this.configuration.getSampleRate().getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT) / 2];
        this.audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                this.configuration.getSampleRate().getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                this.buffer.length);

        if(this.audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(RecordingTask.LOG_TAG,
					"Could not initialize AudioRecord object");
            return false;
        }
        return true;
    }

    /**
     * This method tries to start the recording of the {@link AudioRecord}
	 * instance. If the instance is not initialized, an exceptions is thrown by
	 * {@link AudioRecord} and the {@link RecordingTask} is shutting down.
     */
    private void startRecording() {
        Log.d(RecordingTask.LOG_TAG, "Start Recording Task");

        try {
            this.audioRecord.startRecording();
        } catch (IllegalStateException e) {
            Log.e(RecordingTask.LOG_TAG,
					e.getMessage() + "\t Terminate recording task.");
            this.shutdown();
        }
    }

    /**
     * This method keeps recording until the shutdown flag is true or
	 * {@link AudioRecord} returns an error code. Both ways result in a shutdown
	 * of the {@link RecordingTask}.
     */
    private void record() {
        int samplesRecorded = 0;
        while(!this.shutdown) {
            if((samplesRecorded =
                    this.audioRecord.read(
							this.buffer, 0, this.buffer.length)) <= 0) {
                Log.e(RecordingTask.LOG_TAG,
						"Error occured while reading from AudioRecord. " +
								"AudioRecord error code is " + samplesRecorded +
								"\nTerminate recording task.");
                this.shutdown();
            } else {
                this.sampleBuffer.addSamples(this.buffer);
            }
        }
    }

    /**
     * AudioRecord holds native resources that need to be release. When the
	 * resources are release, the reference is set to null.
     */
    private void releaseAudioRecord() {
        Log.d(RecordingTask.LOG_TAG, "Release native AudioRecord resources.");

        this.audioRecord.release();
        this.audioRecord = null;
    }

    @Override
    public void run() {
        this.startRecording();
        this.record();
        this.releaseAudioRecord();
    }
}
