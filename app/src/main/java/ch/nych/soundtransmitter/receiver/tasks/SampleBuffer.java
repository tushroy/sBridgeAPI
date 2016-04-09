package ch.nych.soundtransmitter.receiver.tasks;

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

    public int addSamples(final short[] samples) {

        return 0;
    }

    public short[] getNextWindow() {

        return null;
    }
}
