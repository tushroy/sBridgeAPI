package ch.nych.soundtransmitter.receiver.tasks;

import ch.nych.soundtransmitter.receiver.Receiver;

/**
 * Created by nych on 4/9/16.
 */
public abstract class ReceiverTask implements Runnable {
    protected Receiver receiver = null;
    protected boolean shutdown = false;

    public ReceiverTask(final Receiver receiver) {
        this.receiver = receiver;
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
