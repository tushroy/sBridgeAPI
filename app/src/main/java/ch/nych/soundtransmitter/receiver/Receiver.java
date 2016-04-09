package ch.nych.soundtransmitter.receiver;

import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.receiver.tasks.recording.*;
import ch.nych.soundtransmitter.receiver.tasks.recording.RecordingTask;
import ch.nych.soundtransmitter.receiver.tasks.transformation.TransformationTask;

/**
 * Created by nych on 4/7/16.
 */
public class Receiver {
    private SampleBuffer sampleBuffer = null;
    private ReceiverTask[] receiverTasks = null;
    private Thread[] workingThreads = null;

    public Receiver() {
        this.sampleBuffer = new SampleBuffer(9600);
        this.initTasks();
    }

    private void initTasks() {
        this.receiverTasks = new ReceiverTask[]{
                new RecordingTask(this),
                new TransformationTask(this)
        };
        this.workingThreads = new Thread[]{
                new Thread(this.receiverTasks[0]),
                new Thread(this.receiverTasks[1])
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

    public SampleBuffer getSampleBuffer() {
        return this.sampleBuffer;
    }
}
