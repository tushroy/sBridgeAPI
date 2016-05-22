package ch.nych.soundtransceiver.receiver.tasks.transformation;

import android.util.Log;

import java.util.Arrays;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.receiver.tasks.ReceiverTask;
import ch.nych.soundtransceiver.receiver.tasks.SampleBuffer;
import ch.nych.soundtransceiver.util.Configuration;
import ch.nych.soundtransceiver.util.Message;

/**
 * This class implements the signal transformation from the time domain to
 * the frequency domain. The transformation is done with the Goertzel
 * algorithm implemented by the {@link Goertzel} class. Instead of scanning
 * the whole spectrum, the Goertzel is aligned to a specific frequency range.
 * Because of the lower complexity it is more efficient than e FFT. The
 * detectMessageBegin() is analyzing the incoming window until it detects the
 * presence of the frame start. Once the beginning is detected, the
 * recordMessage() method is analyzing the carrier frequencies until the end
 * of the frame is detected.
 */
public class TransformationTask extends ReceiverTask {

    /**
     *
     */
    private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
			":TransTask";

    /**
     * Local reference to the shared sampleBuffer object
     */
    private SampleBuffer sampleBuffer = null;

    /**
     * The configured window function for the sample preprocessing
     */
    private double[] windowFunctionValues = null;

    /**
     * The Goertzel algorithm instances
     */
    private Goertzel[] goertzels = null;

    /**
     *
     */
    private double[] buffer = null;

    /**
     *
     * @param receiver
     */
    public TransformationTask(final Receiver receiver) {
        super(receiver);
    }

    @Override
    public boolean initTask() {
        Log.d(TransformationTask.LOG_TAG, "Initialize TransformationTask");

        this.sampleBuffer = this.receiver.getSampleBuffer();
        this.goertzels = new Goertzel[
				this.configuration.getTransmissionMode().getNumOfChannels()];
        this.windowFunctionValues =
				WindowFunction.getWindowFunctionValues(this.configuration);

        for(int i = 0; i < this.goertzels.length; i++) {
            this.goertzels[i] = new Goertzel(
                    this.configuration.getSampleRate().getSampleRate(),
                    this.configuration.getFrequencies()[i],
                    this.configuration.getWindowSize());
        }

        int bufferSize = (this.configuration.getControlToneSize() /
                this.configuration.getWindowSize()) *
                this.configuration.getOverlappingFactor();

        this.buffer = new double[bufferSize];

        return true;
    }

	/**
	 *
	 */
    private void skipWindows() {
        int n = configuration.getInterFrameGap();
        for(int i = 0; i < n;) {
            if(this.sampleBuffer.getNextWindow() != null) {
                i++;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(TransformationTask.LOG_TAG, e.getMessage());
                }
            }
        }
    }

	public void shrinkArray(final double[][] frequencyDomainData, final int
			indexOfLastValue) {
		if(indexOfLastValue >= this.configuration.getMaxFrameSize()) {
			return;
		}
		for(int i = 0; i < frequencyDomainData.length; i++) {
			frequencyDomainData[i] =
				Arrays.copyOf(frequencyDomainData[i], indexOfLastValue + 1);
		}
	}

    /**
     * This method is still in progress
     */
    private double detectMessageBegin() {
        Goertzel listener = this.goertzels[0];
        double threshold = this.configuration.getReceiverThreshold();
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
                for(int i = 0; i < this.buffer.length; i++) {
                    sum += this.buffer[i];
                }
                if(sum > threshold  && temp < this.buffer[this.buffer.length - 1]) {
                    Log.d(TransformationTask.LOG_TAG, "Message detected");
                    for(int i = 0; i < this.buffer.length; i++) {
                        this.buffer[i] = 0;
                    }
                    return sum;
                } else {
                    for(int i = (this.buffer.length - 1); i > 0 ; i--) {
                        this.buffer[i] = this.buffer[i - 1];
                    }
                    this.buffer[0] = temp;
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(TransformationTask.LOG_TAG, e.getMessage());
                }
            }
        }
        return -1;
    }

    /**
     * This method is still in progress
     */
    private Message recordMessage(final double volume) {
        Log.d(TransformationTask.LOG_TAG, "Start recording frame");
        double threshold = volume / 5;
        int maxFrameSize = this.configuration.getMaxFrameSize();
		Message message = new Message(Message.MessageState.RECEIVED);
		double[][] frequencyDomainData = new double[this.configuration
				.getTransmissionMode().getNumOfChannels()][this.configuration
				.getMaxFrameSize()];
        double sum = 0.0;
        short[] window = null;
        int listener = 0;

		int i = 0;
		while(i < maxFrameSize) {
            if((window = this.sampleBuffer.getNextWindow()) != null &&
					!this.shutdown) {
                this.preprocessWindow(window);
                for(int j = 0; j < this.goertzels.length; j++) {
                    this.goertzels[j].processSamples(window);

					frequencyDomainData[j][i] = this.goertzels[j]
							.getMagnitudeSquared();

                    this.goertzels[j].resetGoertzel();
                }
				sum = frequencyDomainData[listener][i];
                for(int j = 0; j < this.buffer.length; j++) {
                    sum += this.buffer[j];
                }

                if(sum > threshold && i > 20) {
                    Log.d(TransformationTask.LOG_TAG, "Detected end of frame");
                    break;
                } else {
                    for(int j = (this.buffer.length - 1); j > 0 ; j--) {
                        this.buffer[j] = this.buffer[j - 1];
                    }
					this.buffer[0] = frequencyDomainData[listener][i];
                }
                i++;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Log.e(TransformationTask.LOG_TAG, e.getMessage());
                }
            }
        }
        for(int j = 0; j < this.buffer.length; j++) {
            this.buffer[j] = 0;
        }
        this.skipWindows();
		this.shrinkArray(frequencyDomainData, i);
		message.setFrequencyDomainData(frequencyDomainData);
        Log.d(TransformationTask.LOG_TAG, "Done recording message. Size: " +
                message.getFrequencyDomainData
						()[0].length);
        return message;
    }

    /**
     * The preprocessing of the samples with a window function can improve
     * the result of the transformation. Every sample of the window is
     * multiplied with its corresponding window function value.
     * @param window the window that needs to be preprocessed
     */
    private void preprocessWindow(final short[] window) {
        for(int i = 0; i < window.length; i++) {
            window[i] *= this.windowFunctionValues[i];
        }
    }


    @Override
    public void run() {
        double volume = 0.0;
        Message message = null;
        while(!this.shutdown) {
            volume = this.detectMessageBegin();
            if(volume > 0) {
                message = this.recordMessage(volume);
                this.receiver.callback(message);
            }
        }
    }
}
