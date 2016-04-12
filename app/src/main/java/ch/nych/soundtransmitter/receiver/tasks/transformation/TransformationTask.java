package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.util.Log;

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
        short[] window = this.sampleBuffer.getNextWindow();
        double[] magnitude = new double[this.goertzels.length];

        if(window == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            for(int i = 0; i < window.length; i++) {
                window[i] *= this.windowFunction[i];
            }
            for(int i = 0; i < window.length; i++) {
                for(Goertzel g : this.goertzels) {
                    g.processSample(window[i]);
                }
            }
            for (int i = 0; i < magnitude.length; i++) {
                magnitude[i] = this.goertzels[i].getMagnitudeSquared();
                this.goertzels[i].resetGoertzel();
            }
        }
    }

    @Override
    public void run() {
        while(!this.shutdown) {
            this.processWindow();
        }
        while(this.sampleBuffer.getNextWindow() != null) {
            this.processWindow();
        }
    }
}
