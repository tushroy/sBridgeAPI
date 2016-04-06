package ch.nych.soundtransmitter.transmitter.tasks;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.message.Message;

/**
 * Created by nych on 4/6/16.
 */
public abstract class TransmissionTask implements Runnable {
    public final static int PREPARATION_TASK = 0;
    public final static int MODULATION_TASK = 1;
    public final static int SENDING_TASK = 2;

    protected int taskType = -1;
    protected Transmitter transmitter = null;
    protected Message message = null;

    public TransmissionTask(Transmitter transmitter, Message message, int taskType) {
        this.transmitter = transmitter;
        this.message = message;
        this.taskType = taskType;
    }

    protected abstract TransmissionTask getNextTask();

    protected void transmitterCallback() {
        this.transmitter.callback(this.getNextTask());
    }

    public int getTaskType() {
        return this.taskType;
    }
}
