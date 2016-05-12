package ch.nych.soundtransceiver.receiver.tasks;

import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.nych.soundtransceiver.util.Configuration;

/**
 *
 */
public class SampleBuffer {

    /**
     *
     */
    private final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
            ":SampleBuffer";

    /**
     *
     */
    private int head = 0;

    /**
     *
     */
    private int tail = 0;

    /**
     * Number of samples to return
     */
    private int windowSize = 0;

    /**
     * Number of positions the head is shifted
     */
    private int overlappingValue = 0;

    /**
     * Array containing the sample values
     */
    private short[] sampleBuffer = null;

    /**
     *
     */
    private Lock lock = null;

    /**
     * The SampleBuffer constructor initialize the member variable and
     * calculates the
     * overllapingValue.
     *
     * @param configuration
     */
    public SampleBuffer(final Configuration configuration) {
        this.windowSize = configuration.getWindowSize();
        this.overlappingValue = configuration.getWindowSize();
        this.overlappingValue /= configuration.getOverlappingFactor();

        this.sampleBuffer = new short[configuration.getSampleBufferSize()];
        this.lock = new ReentrantLock(true);
        Log.d(SampleBuffer.LOG_TAG,
				"Initialize new SampleBuffer with the size of: " +
                this.sampleBuffer.length + "\n\twindowSize:\t\t" +
				this.windowSize + "\n\toverlappingValue:\t" +
				this.overlappingValue);
    }

    /**
     * Sets the head to zero and reduces the tail for the amount of the head
	 * value.
     * @param numberOfSamples
     */
    private void shiftLeft(final int numberOfSamples) {
        int amount = 0;
        if(numberOfSamples < this.head) {
            amount = this.head;
        } else {
            amount = this.tail - this.sampleBuffer.length + numberOfSamples;
        }

        for(int i = amount; i < this.tail; i++) {
            this.sampleBuffer[i-this.head] = this.sampleBuffer[i];
        }

        this.tail -= amount;
        this.head = 0;
    }

    /**
     *
     * @param samples
     */
    public void addSamples(final short[] samples) {
        this.lock.lock();
        try {
            if((this.tail + samples.length) > this.sampleBuffer.length) {
                this.shiftLeft(samples.length);
            }

            for(int i = 0; i < samples.length; i++) {
                this.sampleBuffer[this.tail++] = samples[i];
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     *
     * @return
     */
    public short[] getNextWindow() {
        this.lock.lock();
        short[] window = null;
        try {
            if((this.head + this.windowSize) <= this.tail) {
                window = Arrays.copyOfRange(this.sampleBuffer,
                        this.head,
                        (this.head + this.windowSize));
                this.head += this.overlappingValue;
            }
        } finally {
            this.lock.unlock();
        }
        return window;
    }
}
