package ch.nych.soundtransceiver.receiver.tasks;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.util.Configuration;

/**
 * Created by nych on 4/9/16.
 */
public abstract class ReceiverTask implements Runnable {

    /**
     *
     */
    protected Receiver receiver = null;

    /**
     *
     */
    protected Configuration configuration = null;

    /**
     *
     */
    protected volatile boolean shutdown = false;

    /**
     *
     * @param receiver
     */
    public ReceiverTask(final Receiver receiver) {
        this.receiver = receiver;
        this.configuration = this.receiver.getConfiguration();
    }

    /**
     *
     * @return
     */
    public abstract boolean initTask();

    /**
     *
     */
    public void shutdown() {
        this.shutdown = true;
    }
}
