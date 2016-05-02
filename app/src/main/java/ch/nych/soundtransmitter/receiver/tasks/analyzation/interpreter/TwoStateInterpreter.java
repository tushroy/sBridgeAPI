package ch.nych.soundtransmitter.receiver.tasks.analyzation.interpreter;

import java.util.ArrayList;
import java.util.List;

import ch.nych.soundtransmitter.receiver.tasks.Frame;

/**
 * Created by nych on 5/1/16.
 */
public class TwoStateInterpreter extends Interpreter {

    public TwoStateInterpreter(final Frame frame) {
        super(frame);
    }

    private List<Byte> decodeBytes(List<Byte> list) {
        int sum = 0;
        List<Byte> bytes = new ArrayList<Byte>();

        for(int i = 1; i <= list.size(); i++) {
            sum += list.get(i - 1);

            if(i % 3 == 0) {
                if(sum >= 0 && sum <= 1) {
                    bytes.add((byte) 0);
                } else if(sum > 1) {
                    bytes.add((byte) 1);
                }
                sum = 0;
            }
        }
        return bytes;
    }

    @Override
    protected List<Byte> mapData() {
        List<Byte> list = new ArrayList<Byte>();
        int index = 0;

        for(int i = 0; i < this.frameData[0].length; i++) {
            index = getMaxInRow(i);
            if(index == 1 || index == 3 || index == 5) {
                list.add((byte) 0);
            } else if(index == 2 || index == 4 || index == 6) {
                list.add((byte) 1);
            }
        }
        return this.decodeBytes(list);
    }
}
