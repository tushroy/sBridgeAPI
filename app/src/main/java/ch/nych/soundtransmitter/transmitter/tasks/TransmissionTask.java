package ch.nych.soundtransmitter.transmitter.tasks;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/6/16.
 */
public abstract class TransmissionTask implements Runnable {

    /**
     *
     */
    public final static int MODULATION_TASK = 0;

    /**
     *
     */
    public final static int SENDING_TASK = 1;

    /**
     *
     */
    public final static int NOTIFICATION_TASK = 2;

    /**
     *
     */
    protected int taskType = -1;

    /**
     *
     */
    protected Transmitter transmitter = null;

    /**
     *
     */
    protected Configuration configuration = null;

    /**
     *
     */
    protected Message message = null;

    /**
     *
     * @return
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     *
     * @param transmitter
     * @param message
     * @param taskType
     */
    public TransmissionTask(
            final Transmitter transmitter,
            final Message message,
            final int taskType) {
        this.transmitter = transmitter;
        this.configuration = transmitter.getConfiguration();
        this.message = message;
        this.taskType = taskType;
    }

    /**
     *
     * @return
     */
    protected abstract TransmissionTask getNextTask();

    /**
     *
     */
    protected void transmitterCallback() {
        this.transmitter.callback(this.getNextTask());
    }

    /**
     *
     * @return
     */
    public int getTaskType() {
        return this.taskType;
    }
}
