package ch.nych.soundtransmitter.receiver.tasks.analyzation.interpreter;

import android.util.Log;

import java.util.List;

import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 5/1/16.
 */
public abstract class Interpreter {

    protected final String logTag = Configuration.LOG_TAG + ":Interpreter";

    protected Frame frame = null;
    protected double[][] frameData = null;

    public Interpreter(final Frame frame) {
        this.frame = frame;
        this.frameData = this.frame.getProcessedData();
    }

    protected int getMaxInRow(final int index) {
        int maxIndex = 0;
        for(int i = 0; i < this.frameData.length; i++) {
            if(this.frameData[i][index] > this.frameData[maxIndex][index]) {
                maxIndex = i;
            }
        }
        if(this.frameData[maxIndex][index] <= 0) {
            maxIndex = -1;
        }
        return maxIndex;
    }

    private boolean frameValid(final List<Byte> list) {
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

    private byte[] mergeBytes(List<Byte> list) {
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

    protected abstract List<Byte> mapData();

    public void interpretData() {
        List<Byte> list = this.mapData();
        if(this.frameValid(list)) {
            this.frame.setDataBytes(this.mergeBytes(list));
            this.frame.setState(Frame.ANALYZED_SUCCESSFULLY);
        } else {
            this.frame.setState(Frame.FRAME_CORRUPTED);
        }
    }
}
