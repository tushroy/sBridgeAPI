package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.util.Log;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;

/**
 * This class implements the signal transformation from the time domain to the frequency domain.
 * The transformation is done with the Goertzel algorithm implemented by the {@link Goertzel} class.
 * Instead of scanning the whole spectrum, the Goertzel is aligned to a specific frequency range.
 * Because of the lower complexity it is more efficient than e FFT.
 * The detectFrameBegin() is analyzing the incoming window until it detects the presence of the
 * frame start. Once the beginning is detected, the recordFrame() method is analyzing the carrier
 * frequencies until the end of the frame is detected.
 *
 *                                          TODO
 *
 *
 */
public class TransformationTask extends ReceiverTask {

    /**
     * Local reference to the shared sampleBuffer object
     */
    private SampleBuffer sampleBuffer = null;

    /**
     * The configured window function for the sample preprocessing
     */
    private double[] windowFunction = null;

    /**
     * The Goertzel algorithm instances
     */
    private Goertzel[] goertzels = null;

    public TransformationTask(final Receiver receiver) {
        super(receiver);
    }

    @Override
    public boolean initTask() {
        Log.d(this.logTag, "Initialize TransformationTask");

        this.sampleBuffer = this.receiver.getSampleBuffer();

        // TODO: 4/21/16  May be updated if the controlling frequency is not necessary
        this.goertzels = new Goertzel[this.configuration.getTransmissionMode()];
        // TODO: 4/21/16 If the concept of the window function is changed, don't forget to change
        this.windowFunction = WindowFunction.getWindowFunction(this.configuration);

        for(int i = 0; i < this.goertzels.length; i++) {
            this.goertzels[i] = new Goertzel(
                    this.configuration.getSampleRate(),
                    this.configuration.getFrequencies()[i],
                    this.configuration.getWindowSize());
        }
        return true;
    }

    /**
     * This method is still in progress
     */
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

    /**
     * This method is still in progress
     */
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

    /**
     * The preprocessing of the samples with a window function can improve the result of the
     * transformation. Every sample of the window is multiplied with its corresponding window
     * function value
     * @param window the window that needs to be preprocessed
     */
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
