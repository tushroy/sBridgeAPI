package ch.nych.soundtransmitter.receiver.tasks.recording;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.receiver.tasks.SampleBuffer;

/**
 * Created by nych on 4/9/16.
 */
public class RecordingTask extends ReceiverTask {

    private SampleBuffer sampleBuffer = null;

    public RecordingTask(Receiver receiver) {
        super(receiver);
        this.sampleBuffer = this.receiver.getSampleBuffer();
    }

    @Override
    public void run() {
        while(!this.shutdown) {
            //capture audio
        }
    }
}
