package ch.nych.soundtransmitter.receiver.tasks.analyzation.interpreter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 5/1/16.
 */
public class TwoStateInterpreter extends Interpreter {

    private final String logTag = Configuration.LOG_TAG + ":Interpreter";

    public TwoStateInterpreter(final Frame frame) {
        super(frame);
    }

    private List<Byte> mapData() {
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
        return list;
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

    public byte[] mergeBytes(List<Byte> list) {
        byte[] bytes = new byte[list.size() / 8];
        int temp = 0;
        int j = 0;

        for(int i = 1; i <= list.size(); i++) {
            temp = temp << 1;
            temp = temp | list.get(i - 1);
            if(i % 8 == 0) {
                Log.d(this.logTag, Integer.toHexString(temp));
                bytes[j++] = (byte) temp;
                temp = 0;
            }
        }
        return bytes;
    }

    public boolean frameValid(final List<Byte> list) {
        if(list.get(0) != 0 ||
                list.get(1) != 1 ||
                list.get(2) != 0 ||
                list.get(3) != 1 ||
                list.get(4) != 1) {
            Log.d(this.logTag, "Detected Frame is not valid");
            return false;
        }
        return true;
    }

    @Override
    public boolean interpretBytes() {
        List<Byte> list = this.mapData();
        List<Byte> dataBytes = this.decodeBytes(list);
        if(!this.frameValid(dataBytes)) {
            this.frame.setState(Frame.FRAME_CORRUPTED);
            return false;
        }
        this.frame.setDataBytes(this.mergeBytes(dataBytes));
        this.frame.setState(Frame.ANALYZED_SUCCESSFULLY);
        return true;
    }
}
