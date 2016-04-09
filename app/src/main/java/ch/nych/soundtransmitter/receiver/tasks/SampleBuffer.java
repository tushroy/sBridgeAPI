package ch.nych.soundtransmitter.receiver.tasks;

import android.util.Log;

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
        Log.d("MyTag", "\tBuffer overflow, shift left");
        int amount = 0;
        if(numberOfSamples < this.head) {
            amount = this.head;
        } else {
            amount = this.tail - this.sampleBuffer.length + numberOfSamples;
            Log.w("MyTag", "Buffer size to low, lost: " + (numberOfSamples - this.head) + " samples");
        }

        for(int i = amount; i < this.tail; i++) {
            this.sampleBuffer[i-this.head] = this.sampleBuffer[i];
        }
        Log.d("MyTag", "\tShifted left");
        Log.d("MyTag", "\tHead_old: " + this.head);
        Log.d("MyTag", "\tTail_old: " + this.tail);
        this.tail -= amount;
        this.head = 0;
        Log.d("MyTag", "\tHead_new: " + this.head);
        Log.d("MyTag", "\tTail_new: " + this.tail);
    }

    public void addSamples(final short[] samples) {
        this.lock.lock();
        Log.d("MyTag", "\n--------------------------");
        Log.d("MyTag", "Added " + samples.length +" samples");
        Log.d("MyTag", "Head_old:\t" + this.head);
        Log.d("MyTag", "Tail_old:\t" + this.tail);
        try {
            if((this.tail + samples.length) > this.sampleBuffer.length) {
                this.shiftLeft(samples.length);
            }

            for(int i = 0; i < samples.length; i++) {
                this.sampleBuffer[this.tail++] = samples[i];
            }

            Log.d("MyTag", "Head_new: " + this.head);
            Log.d("MyTag", "Tail_new: " + this.tail);
            Log.d("MyTag", "-----------------------------");
        } finally {
            this.lock.unlock();
        }
    }

    public short[] getNextWindow() {
        this.lock.lock();
        short[] window = null;
        try {
            Log.d("MyTag", "\n--------------------------");
            if((this.head + 480) <= this.tail) {
                Log.d("MyTag", "Get next window");
                Log.d("MyTag", "Head_old: " + this.head);
                Log.d("MyTag", "Tail_old: " + this.tail);
                window = Arrays.copyOfRange(this.sampleBuffer, this.head, this.head + 480);
                this.head += 480;
                Log.d("MyTag", "Head_new: " + this.head);
                Log.d("MyTag", "Tail_new: " + this.tail);
                Log.d("MyTag", "--------------------------");
            } else {
                Log.d("MyTag", "No Window available at the moment");
            }
        } finally {
            this.lock.unlock();
        }
        return window;
    }
}
