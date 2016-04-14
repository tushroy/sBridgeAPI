package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.util.Log;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/9/16.
 */
public class TransformationTask extends ReceiverTask {

    private SampleBuffer sampleBuffer = null;
    private double[] windowFunction = null;
    private Goertzel[] goertzels = null;

    public TransformationTask(final Receiver receiver) {
        super(receiver);
        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.initTransformationTask();
    }

    private void initTransformationTask() {
        this.goertzels = new Goertzel[this.configuration.getFrequencies().length];
        for(int i = 0; i < this.goertzels.length; i++) {
            this.goertzels[i] = new Goertzel(
                    this.configuration.getSampleRate(),
                    this.configuration.getFrequencies()[i],
                    this.configuration.getBlocksize());
        }

        this.windowFunction = WindowFunction.getWindowFunction(this.configuration);
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
