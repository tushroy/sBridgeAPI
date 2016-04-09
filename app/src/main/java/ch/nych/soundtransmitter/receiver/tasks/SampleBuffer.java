package ch.nych.soundtransmitter.receiver.tasks;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nych on 4/9/16.
 */
public class SampleBuffer {
    public final static int DEFAULT_SIZE = 9600;

    private int head = 0;
    private int tail = 0;
    private short[] sampleBuffer = null;
    private Lock lock = null;

    public SampleBuffer(final int size) {
        if(size <= 0) {
            this.sampleBuffer = new short[SampleBuffer.DEFAULT_SIZE];
        } else {
            this.sampleBuffer = new short[size];
        }
        this.lock = new ReentrantLock(true);
    }

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

    public int addSamples(final short[] samples) {
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
        return 0;
    }

    public short[] getNextWindow() {
        this.lock.lock();
        short[] window = null;
        try {
            if((this.head + 480) <= this.tail) {
                window = Arrays.copyOfRange(this.sampleBuffer, this.head, this.head + 480);
                this.head += 480;
            }
        } finally {
            this.lock.unlock();
        }
        return window;
    }
}
