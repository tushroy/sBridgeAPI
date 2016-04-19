package ch.nych.soundtransmitter.receiver;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.receiver.tasks.analyzation.AnalyzationTask;
import ch.nych.soundtransmitter.receiver.tasks.recording.RecordingTask;
import ch.nych.soundtransmitter.receiver.tasks.transformation.TransformationTask;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/7/16.
 */
public class Receiver {

    private final String logTag = Configuration.LOG_TAG;
    private Configuration configuration = null;
    private SampleBuffer sampleBuffer = null;
    private ConcurrentLinkedQueue<double[]> magnitudeBuffer = new ConcurrentLinkedQueue<double[]>();
    private ReceiverTask[] receiverTasks = null;
    private Thread[] workingThreads = null;

    public int initReceiver(final Configuration configuration) {

        Log.i(this.logTag, "Initialize Receiver");

        if(configuration == null) {
            Log.e(this.logTag, "Invalid Configuration, Receiver is not ready");
            return -1;
        } else {
            this.configuration = configuration;
        }

        this.sampleBuffer = new SampleBuffer(this.configuration);

        this.initTasks();

        return 0;
    }

    private void initTasks() {
        Log.d(this.logTag, "Initialize Tasks and Executing Threads");
        this.receiverTasks = new ReceiverTask[]{
                new RecordingTask(this),
                new TransformationTask(this),
                new AnalyzationTask(this)
        };
        this.workingThreads = new Thread[]{
                new Thread(this.receiverTasks[0]),
                new Thread(this.receiverTasks[1]),
                new Thread(this.receiverTasks[2])
        };
    }

    public void startReceiver() {
        for(Thread thread : this.workingThreads) {
            thread.start();
        }
    }

    public void shutdownReceiver() {
        this.receiverTasks[0].shutdown();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.receiverTasks[1].shutdown();
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public SampleBuffer getSampleBuffer() {
        return this.sampleBuffer;
    }

    public ConcurrentLinkedQueue<double[]> getMagnitudeBuffer() {
        return this.magnitudeBuffer;
    }
}
