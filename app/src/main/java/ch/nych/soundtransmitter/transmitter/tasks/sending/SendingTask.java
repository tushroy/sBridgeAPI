package ch.nych.soundtransmitter.transmitter.tasks.sending;

import android.media.AudioTrack;
import android.util.Log;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransmitter.transmitter.tasks.notification.NotificationTask;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * This class is responsible for the transmission of the message, by playing the modulated audio
 * signal.
 * Created by nych on 4/6/16.
 */
public class SendingTask extends TransmissionTask {

    /**
     * Local log tag
     */
    private final static String LOG_TAG = Configuration.LOG_TAG + ":sendingTask";

    /**
     * Default constructor
     * @param transmitter    the reference to the calling {@link Transmitter} instance is used for
     *                       the shared resources and the callback.
     * @param message        The {@link Message} instance, containing the data to modulate
     */
    public SendingTask(final Transmitter transmitter, final Message message) {
        super(transmitter, message, TaskType.SENDING);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return new NotificationTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        Log.d(SendingTask.LOG_TAG, "sending tone sequence");
        AudioTrack audioTrack = this.transmitter.getAudioTrack();
        Tone[] toneSequence = this.message.getToneSequence(true);

        int totalSamplesSent = 0;
        try {
            audioTrack.play();
            this.message.setMessageState(Message.MessageState.SENDING);
            int sent = 0;
            for(Tone tone : toneSequence) {
                Log.d(SendingTask.LOG_TAG, "Sending tone: " + tone.getFrequency());
                sent = audioTrack.write(tone.getSamples(), 0, (int) tone.getLength());
                if(sent <= 0) {
                    Log.e(SendingTask.LOG_TAG, "Error during message playing. AudioTrack error code is: " +
                            sent);
                    this.message.setMessageState(Message.MessageState.ABORT);
                    break;
                }
                totalSamplesSent += sent;
            }
        } catch (IllegalStateException e) {
            Log.e(SendingTask.LOG_TAG, e.getMessage());
            this.message.setMessageState(Message.MessageState.ABORT);
        }
        this.message.setMessageState(Message.MessageState.SENT);
        this.transmitter.getAudioTrack().stop();
        Log.d(SendingTask.LOG_TAG, "sent a total of " + totalSamplesSent + " samples");
        this.transmitterCallback();
    }
}
