package ch.nych.soundtransmitter.receiver.tasks;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.nych.soundtransmitter.util.Configuration;

/**
 *
 */
public class SampleBuffer {

    //API Log Tag
    private final String logTag = Configuration.LOG_TAG;

    //this is just a temporary flag until the class has proven
    private final boolean log = false;


    private int head = 0;
    private int tail = 0;

    //Number of samples to return
    private int windowSize = 0;

    //Number of positions the head is shifted
    private int overlappingValue = 0;

    //Array containing the sample values
    private short[] sampleBuffer = null;

    private Lock lock = null;

    /**
     * The SampleBuffer constructor initialize the member variable and calculates the
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
        Log.d(this.logTag, "Initialize new SampleBuffer with the size of: " +
                configuration.getSampleBufferSize() + "\n\twindowSize:\t\t" + this.windowSize +
                "\n\toverlappingValue:\t" + this.overlappingValue);
    }

    /**
     * Sets the head to zero and reduces the tail for the amount of the head value.
     * @param numberOfSamples
     */
    private void shiftLeft(final int numberOfSamples) {
        if(this.log) {
            Log.d(this.logTag, "\tReached end of Array, shift left");
        }
        int amount = 0;
        if(numberOfSamples < this.head) {
            amount = this.head;
        } else {
            amount = this.tail - this.sampleBuffer.length + numberOfSamples;
            if(this.log) {
                Log.w(this.logTag, "\t\tSampleBuffer size to low, lost: " +
                        (numberOfSamples - this.head) + " samples\n" +
                        "\t\tIncrease size of SampleBuffer (Configuration.setSampleBufferSize())");
            }
        }

        for(int i = amount; i < this.tail; i++) {
            this.sampleBuffer[i-this.head] = this.sampleBuffer[i];
        }

        if(this.log) {
            Log.d(this.logTag,
                "\tShifted left" + "\n\t\tHead_old: " + this.head + "\n\t\tTail_old: " + this.tail);
        }
        this.tail -= amount;
        this.head = 0;

        if(this.log) {
            Log.d(this.logTag, "\t\tHead_new: " + this.head + "\n\t\tTail_new: " + this.tail);
        }
    }

    public void addSamples(final short[] samples) {
        this.lock.lock();
        if(this.log) {
            Log.d(this.logTag, "\n--------------------------");
        }
        try {
            if((this.tail + samples.length) > this.sampleBuffer.length) {
                this.shiftLeft(samples.length);
            }

            if(this.log) {
                Log.d(this.logTag, "Head_old:\t" + this.head + "\nTail_old:\t" + this.tail);
            }

            for(int i = 0; i < samples.length; i++) {
                this.sampleBuffer[this.tail++] = samples[i];
            }

            if(this.log) {
                Log.d(this.logTag, "Head_new:\t" + this.head + "\nTail_new:\t" + this.tail +
                        "\nAdded " + samples.length +" samples" + "\n-----------------------------");
            }
        } finally {
            this.lock.unlock();
        }
    }

    public short[] getNextWindow() {
        this.lock.lock();
        short[] window = null;
        try {
            if(this.log) {
                Log.d(this.logTag, "\n--------------------------");
            }
            if((this.head + this.windowSize) <= this.tail) {
                if(this.log) {
                    Log.d(this.logTag, "Get next window" + "\nHead_old:\t" + this.head +
                            "\nTail_old:\t" + this.tail);
                }
                window = Arrays.copyOfRange(this.sampleBuffer,
                        this.head,
                        (this.head + this.windowSize));
                this.head += this.overlappingValue;
                if(this.log) {
                    Log.d(this.logTag, "Head_new:\t" + this.head + "\nTail_new:\t" + this.tail);
                }

            } else {
                if(this.log) {
                    Log.d(this.logTag, "No Window available at the moment");
                }
            }
            if(this.log) {
                Log.d(this.logTag, "--------------------------");
            }
        } finally {
            this.lock.unlock();
        }
        return window;
    }
}
