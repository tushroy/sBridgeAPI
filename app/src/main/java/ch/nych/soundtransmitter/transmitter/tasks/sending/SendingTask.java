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
    private final String logTag = Configuration.LOG_TAG + ":sendTask";

    /**
     * Default constructor
     * @param transmitter    the reference to the calling {@link Transmitter} instance is used for
     *                       the shared resources and the callback.
     * @param message        The {@link Message} instance, containing the data to modulate
     */
    public SendingTask(final Transmitter transmitter, final Message message) {
        super(transmitter, message, TransmissionTask.SENDING_TASK);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return new NotificationTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        Log.d(this.logTag, "sending tone sequence");
        AudioTrack audioTrack = this.transmitter.getAudioTrack();
        Tone[] toneSequence = this.message.getToneSequence(true);

        int totalSamplesSent = 0;
        try {
            audioTrack.play();
            this.message.setState(Message.STATE_SENDING);
            int sent = 0;
            for(Tone tone : toneSequence) {
                Log.d(this.logTag, "Sending tone: " + tone.getFrequency());
                sent = audioTrack.write(tone.getSamples(), 0, (int) tone.getLength());
                if(sent <= 0) {
                    Log.e(this.logTag, "Error during message playing. AudioTrack error code is: " +
                            sent);
                    this.message.setState(Message.STATE_ABORT);
                    break;
                }
                totalSamplesSent += sent;
            }
        } catch (IllegalStateException e) {
            Log.e(this.logTag, e.getMessage());
            this.message.setState(Message.STATE_ABORT);
        }
        this.message.setState(Message.STATE_SENT);
        this.transmitter.getAudioTrack().stop();
        Log.d(this.logTag, "sent a total of " + totalSamplesSent + " samples");
        this.transmitterCallback();
    }
}
