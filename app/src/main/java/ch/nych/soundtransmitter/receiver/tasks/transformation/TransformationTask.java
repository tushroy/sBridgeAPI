package ch.nych.soundtransmitter.receiver.tasks.transformation;

import android.util.Log;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;
import ch.nych.soundtransmitter.util.Configuration;

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
     *
     */
    private final String logTag = Configuration.LOG_TAG + ":TransTask";

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
    private double detectFrameBegin() {
        Goertzel listener = this.goertzels[0];
        double threshold = this.configuration.getReceiverThreshold();
        double[] buffer = new double[8];
        double sum = 0.0;
        double temp = 0.0;
        short[] window = null;

        while (!this.shutdown) {
            if ((window = this.sampleBuffer.getNextWindow()) != null) {
                this.preprocessWindow(window);
                listener.processSamples(window);
                temp = listener.getMagnitudeSquared();
                listener.resetGoertzel();
                sum = temp;
                for(int i = 0; i < buffer.length; i++) {
                    sum += buffer[i];
                }
                if(sum > threshold  && temp < buffer[buffer.length - 1]) {
                    Log.d(this.logTag, "Frame detected");
                    return sum;
                } else {
                    for(int i = (buffer.length - 1); i > 0 ; i--) {
                        buffer[i] = buffer[i - 1];
                    }
                    buffer[0] = temp;
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(this.logTag, e.getMessage());
                }
            }
        }
        return -1;
    }

    /**
     * This method is still in progress
     */
    private Frame recordFrame(final double volume) {
        Log.d(this.logTag, "Start recording frame");
        double threshold = volume / 5;
        int maxFrameSize = this.configuration.getMaxFrameSize();
        Frame frame = new Frame(this.configuration);
        double[] magnitudes = new double[this.goertzels.length];
        double[] buffer = new double[8];
        double sum = 0.0;
        short[] window = null;
        int listener = 0;

        for(int i = 0; i < maxFrameSize;) {
            if((window = this.sampleBuffer.getNextWindow()) != null) {
                this.preprocessWindow(window);
                for(int j = 0; j < this.goertzels.length; j++) {
                    this.goertzels[j].processSamples(window);
                    magnitudes[j] = this.goertzels[j].getMagnitudeSquared();
                    this.goertzels[j].resetGoertzel();
                }
                frame.addDataSet(magnitudes);
                sum = magnitudes[listener];
                for(int j = 0; j < buffer.length; j++) {
                    sum += buffer[j];
                }

                if(sum > threshold) {
                    Log.d(this.logTag, "Detected end of frame");
                    break;
                } else {
                    for(int j = (buffer.length - 1); j > 0 ; j--) {
                        buffer[j] = buffer[j - 1];
                    }
                    buffer[0] = magnitudes[listener];
                }
                i++;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(this.logTag, e.getMessage());
                }
            }
        }
        //Inter frame gap
        for(int i = 0; i < 20;) {
            if(this.sampleBuffer.getNextWindow() != null) {
                i++;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(this.logTag, e.getMessage());
                }
            }
        }
        frame.sealFrame();
        Log.d(this.logTag, "Done recording frame. Size: " + frame.getOriginalData()[0].length);
        return frame;
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
        double volume = 0.0;
        Frame frame = null;
        while(!this.shutdown) {
            volume = this.detectFrameBegin();
            if(volume > 0) {
                frame = this.recordFrame(volume);
                this.receiver.callback(frame);
            }
        }
    }
}
