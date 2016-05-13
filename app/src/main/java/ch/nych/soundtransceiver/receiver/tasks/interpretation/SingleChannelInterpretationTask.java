package ch.nych.soundtransceiver.receiver.tasks.interpretation;

import java.util.List;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.util.Message;

/**
 * Created by nych on 5/4/16.
 */
public class SingleChannelInterpretationTask extends InterpretationTask {

    public SingleChannelInterpretationTask(final Receiver receiver,
                                           final Message message) {
        super(receiver, message);
    }

    @Override
    protected int mapData(final List<Byte> list,
                          final int from,
                          final int size) {
        int index = -1;
        int last = -1;

        for (int i = from; i < this.frequencyDomainData[0].length; i++) {
            if ((index = getMaxInRow(i)) == last ||
                    this.frequencyDomainData[index][i]
                            < this.thresholds[index]) {
                continue;
            }
            if (index == 1) {
                list.add((byte) 0);
                if(list.size() > size) {
                    list.remove(list.size() - 1);
                    return i;
                }
            } else if(index == 2) {
                list.add((byte) 1);
                if(list.size() > size) {
					list.remove(list.size() - 1);
                    return i;
                }
            }
            last = index;
        }
        return this.frequencyDomainData[0].length;
    }
}
