package ch.nych.soundtransceiver.transmitter.tasks;

import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Configuration;

/**
 * Super class for the different transmission tasks. The whole transmission process is divided into
 * different independent tasks. As they implement the {@link Runnable} interface, the different
 * tasks can be processed in parallel. For a successful transmission of the message, the tasks need
 * to be processed in the right order. This is achieved by the identification of the task type and
 * the callback method in {@link Transmitter}. If you are implementing another task, you also need
 * add another identifier for the task. Furthermore, the getNextTask() method of the single tasks
 * need to be reimplemented, so the order is correct.
 *
 * Created by nych on 4/6/16.
 */
public abstract class TransmissionTask implements Runnable {

    /**
     * Task types
     */
    public enum TaskType {MODULATION, SENDING, NOTIFICATION}

    /**
     * Specifies the task type which can either be MODULATION_TASK, SENDING_TASK or
     * NOTIFICATION_TASK
     */
    protected TaskType taskType = null;

    /**
     * The local reference to the calling {@link Transmitter} instance
     */
    protected Transmitter transmitter = null;

    /**
     * The local reference to the shared {@link Configuration} instance
     */
    protected Configuration configuration = null;

    /**
     * The {@link Message} to transmit
     */
    protected Message message = null;

    /**
     * Default constructor of the TransmissionTask super class
     * @param transmitter    the reference to the calling {@link Transmitter} instance is used for
     *                       the shared resources and the callback.
     * @param message        the message to transmit.
     * @param taskType       specifies whether the task is from type MODULATION_TASK, SENDING_TASK
     *                       or NOTIFICATION_TASK
     */
    public TransmissionTask(
            final Transmitter transmitter,
            final Message message,
            final TaskType taskType) {
        this.transmitter = transmitter;
        this.configuration = transmitter.getConfiguration();
        this.message = message;
        this.taskType = taskType;
    }

    /**
     * Getter for the {@link Message} instance
     * @return the {@link Message} instance of the message to send
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * Getter method for the task type.
     * @return either MODULATION_TASK, SENDING_TASK or NOTIFICATION_TASK
     */
    public TaskType getTaskType() {
        return this.taskType;
    }

    /**
     * This method returns the next task in the chain. Every subclass is a task and has a following
     * task. Implementing a new task in the chain, also requires to define where this task should be
     * situated in the process of a message transmission.
     * @return the next task to execute
     */
    protected abstract TransmissionTask getNextTask();

    /**
     * The callback method to the calling {@link Transmitter} instance
     */
    protected void transmitterCallback() {
        this.transmitter.callback(this.getNextTask());
    }
}
