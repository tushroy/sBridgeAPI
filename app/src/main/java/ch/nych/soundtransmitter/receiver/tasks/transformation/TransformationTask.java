package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/9/16.
 */
public class TransformationTask extends ReceiverTask {

    private SampleBuffer sampleBuffer = null;
    private ConcurrentLinkedQueue<double[]> magnitudeBuffer = null;
    private double[] windowFunction = null;
    private Goertzel[] goertzels = null;

    public TransformationTask(final Receiver receiver) {
        super(receiver);
        this.initTransformationTask();
    }

    private void initTransformationTask() {
        Log.d(this.logTag, "Initialize TransformationTask");
        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.magnitudeBuffer = this.receiver.getMagnitudeBuffer();
        this.goertzels = new Goertzel[this.configuration.getFrequencies().length];
        this.windowFunction = WindowFunction.getWindowFunction(this.configuration);

        for(int i = 0; i < this.goertzels.length; i++) {
            this.goertzels[i] = new Goertzel(
                    this.configuration.getSampleRate(),
                    this.configuration.getFrequencies()[i],
                    this.configuration.getWindowSize());
        }
    }

    private void processWindow() {
        short[] window = this.sampleBuffer.getNextWindow();
        double[] magnitude = new double[this.goertzels.length];

        if(window == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(this.logTag, e.getMessage());
            }
        } else {
            //Process samples with windowFunction
            for(int i = 0; i < window.length; i++) {
                window[i] *= this.windowFunction[i];
            }

            //Process samples with the Goertzel algorithm
            for(int i = 0; i < window.length; i++) {
                for(Goertzel g : this.goertzels) {
                    g.processSample(window[i]);
                }
            }
            for (int i = 0; i < magnitude.length; i++) {
                magnitude[i] = this.goertzels[i].getMagnitudeSquared();
                this.goertzels[i].resetGoertzel();
            }
            this.magnitudeBuffer.add(magnitude);
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
