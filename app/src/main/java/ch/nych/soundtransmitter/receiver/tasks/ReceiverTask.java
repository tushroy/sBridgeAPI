package ch.nych.soundtransmitter.receiver.tasks;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.util.Configuration;

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
    protected boolean shutdown = false;

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
