package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.graphics.Bitmap;
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
    
    private void detectFrameBegin() {
        Goertzel listener = this.goertzels[this.configuration.getTransmissionMode() / 2];
        double threshold = 2000000000000.0;
        short[] window = null;

        while(!this.shutdown) {
            if((window = this.sampleBuffer.getNextWindow()) != null) {
                this.preprocessWindow(window);
                for(short sample : window) {
                    listener.processSample(sample);
                }
                if(listener.getMagnitudeSquared() > threshold) {
                    Log.d(this.logTag, "Frame detected");
                    listener.resetGoertzel();
                    return;
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(this.logTag, e.getMessage());
                }
            }
            listener.resetGoertzel();
        }
    }

    private void recordFrame() {
        Frame frame = new Frame(this.configuration);
        Goertzel listener = this.goertzels[this.configuration.getTransmissionMode() / 2];
        double threshold = 2000000000000.0;
        short[] window = null;
        double[] magnitudes = new double[this.goertzels.length];

        while(!this.shutdown || listener.getMagnitudeSquared() < threshold) {
            if((window = this.sampleBuffer.getNextWindow()) != null) {
                this.preprocessWindow(window);
                for(int i = 0; i < this.goertzels.length; i++) {
                    for(short sample : window) {
                        this.goertzels[i].processSample(sample);
                    }
                    magnitudes[i] = this.goertzels[i].getMagnitudeSquared();
                    this.goertzels[i].getMagnitudeSquared();
                }
                frame.addMagnitudeSet(magnitudes);
                // TODO: 4/20/16 Callback or similiar
            }
        }
    }

    private void preprocessWindow(final short[] window) {
        for(int i = 0; i < window.length; i++) {
            window[i] *= this.windowFunction[i];
        }
    }


    @Override
    public void run() {
        while(!this.shutdown) {
            this.detectFrameBegin();
            this.recordFrame();
        }
    }
}
