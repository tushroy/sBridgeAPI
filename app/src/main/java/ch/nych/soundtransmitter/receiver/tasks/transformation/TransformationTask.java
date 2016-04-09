package ch.nych.soundtransmitter.receiver.tasks.transformation;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;

/**
 * Created by nych on 4/9/16.
 */
public class TransformationTask extends ReceiverTask {

    private SampleBuffer sampleBuffer = null;
    private double[] windowFunction = null;
    private Goertzel[] goertzels = null;

    public TransformationTask(Receiver receiver) {
        super(receiver);
        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.windowFunction = WindowFunction.getHammingWindow(480);
        this.initGoertzels();
    }

    private void initGoertzels() {
        this.goertzels = new Goertzel[] {
                new Goertzel(48000, 19600, 480),
                new Goertzel(48000, 19700, 480),
                new Goertzel(48000, 19800, 480),
                new Goertzel(48000, 19900, 480),
                new Goertzel(48000, 20000, 480),
                new Goertzel(48000, 20100, 480),
                new Goertzel(48000, 20200, 480),
                new Goertzel(48000, 20300, 480),
                new Goertzel(48000, 20400, 480)
        };
    }

    private void processWindow() {
        //process window
    }

    @Override
    public void run() {
        while(!this.shutdown) {
            this.processWindow();
        }
    }
}
