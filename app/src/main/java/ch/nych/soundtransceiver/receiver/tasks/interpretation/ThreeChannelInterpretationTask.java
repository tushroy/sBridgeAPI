package ch.nych.soundtransceiver.receiver.tasks.interpretation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.util.Message;

/**
 * Created by nych on 5/4/16.
 */
public class ThreeChannelInterpretationTask extends InterpretationTask {

    public ThreeChannelInterpretationTask(final Receiver receiver,
                                          final Message message) {
        super(receiver, message);
    }

    @Override
    protected List<Byte> interpretMessage() {
        Log.d(InterpretationTask.LOG_TAG, "interpret frequency domain data");

        List<Byte> list = new ArrayList<Byte>();

        int index = -1;
        int last = -1;

        for (int i = 0; i < this.frequencyDomainData[0].length; i++) {
            if ((index = getMaxInRow(i)) == last ||
                    this.frequencyDomainData[index][i]
                            < this.thresholds[index]) {
                continue;
            }
            if (index == 1 || index == 3 || index == 5) {
                list.add((byte) 0);
            } else if(index == 2 || index == 4 || index == 6) {
                list.add((byte) 1);
            }
            last = index;
        }
        return list;
    }
}
