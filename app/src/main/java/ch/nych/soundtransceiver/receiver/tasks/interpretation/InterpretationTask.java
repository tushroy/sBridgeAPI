package ch.nych.soundtransceiver.receiver.tasks.interpretation;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.receiver.tasks.ReceiverTask;
import ch.nych.soundtransceiver.util.Configuration;
import ch.nych.soundtransceiver.util.Message;

/**
 * Created by nych on 5/1/16.
 */
public abstract class InterpretationTask extends ReceiverTask {

    /**
     *
     */
    protected final static String LOG_TAG = Configuration.GLOBAL_LOG_TAG +
            ":IntTask";

    /**
     *
     */
    protected Message message = null;

	/**
	 *
	 */
	protected double[][] frequencyDomainData = null;

    /**
     *
     */
    protected double[] thresholds = null;

    /**
     *
     */
    protected byte[] preamble = null;

    /**
     * @param receiver
     */
    public InterpretationTask(final Receiver receiver, final Message message) {
        super(receiver);
        this.message = message;
    }

    @Override
    public boolean initTask() {
		Log.d(InterpretationTask.LOG_TAG, "initialize InterpretationTask");
        if(this.message == null ||
                this.message.getFrequencyDomainData() == null) {
            return false;
        } else {
            this.frequencyDomainData = this.message.getFrequencyDomainData();
            this.thresholds = new double[this.frequencyDomainData.length];

            this.preamble = this.configuration.getPreamble();
        }
        return true;
    }

    /**
     *
     */
    protected void smoothData() {
		Log.d(InterpretationTask.LOG_TAG, "smooth frequency domain data");
        // TODO: 5/7/16 validation and dynamic
        double[] coefficients = configuration.getFilterCoefficients();
		double[][] copyOfData = new double[this.frequencyDomainData.length][];

		for(int i = 0; i < copyOfData.length; i++) {
			copyOfData[i] = Arrays.copyOf(this.frequencyDomainData[i],
					this.frequencyDomainData[i].length);
		}

        int from = (coefficients.length / 2) + 1;
        int to = this.frequencyDomainData[0].length - from;
        for(int i = 0; i < (this.frequencyDomainData.length); i++) {
            for(int j = from; j < to; j++) {
                this.frequencyDomainData[i][j] +=
						copyOfData[i][j - 2] * coefficients[0];
                this.frequencyDomainData[i][j] +=
						copyOfData[i][j - 1] * coefficients[1];
                this.frequencyDomainData[i][j] *=
						coefficients[2];
                this.frequencyDomainData[i][j] +=
						copyOfData[i][j + 1] * coefficients[3];
                this.frequencyDomainData[i][j] +=
						copyOfData[i][j + 2] * coefficients[4];
            }
        }
    }

    /**
     *
     */
    private void calculateThresholds() {
		Log.d(InterpretationTask.LOG_TAG, "calculate thresholds");
        for(int i = 0; i < this.thresholds.length; i++) {
            for(int j = 0; j < this.frequencyDomainData[i].length; j++) {
                this.thresholds[i] +=
						Math.pow(this.frequencyDomainData[i][j], 2);
            }
            this.thresholds[i] /= this.frequencyDomainData[i].length;
            this.thresholds[i] = Math.sqrt(this.thresholds[i]);
        }
    }

    /**
     *
     * @param index
     * @return
     */
    protected int getMaxInRow(final int index) {
        int maxIndex = 0;
        for(int i = 0; i < this.frequencyDomainData.length; i++) {
            if(this.frequencyDomainData[i][index] >
					this.frequencyDomainData[maxIndex][index]) {
                maxIndex = i;
            }
        }
        if(this.frequencyDomainData[maxIndex][index] <= 0) {
            maxIndex = -1;
        }
        return maxIndex;
    }

    private boolean checkPreamble(List<Byte> dataBytes) {
		Log.d(InterpretationTask.LOG_TAG, "check preamble");
        if(this.preamble.length >= dataBytes.size()) {
            return false;
        }
        for(int i = 0; i < this.preamble.length; i++) {
            if(dataBytes.get(i) != this.preamble[i]) {
                return false;
            }
        }
        for(int i = 0; i < this.preamble.length; i++) {
			dataBytes.remove(0);
		}
		Log.d(InterpretationTask.LOG_TAG, "valid preamble");
        return true;
    }

    /**
     *
     * @param bitList
     * @return
     */
    private byte[] mergeBitsToBytes(final List<Byte> bitList) {
		Log.d(InterpretationTask.LOG_TAG, "merge bits to bytes");
		byte[] bytes = new byte[bitList.size() / 8];
        int temp = 0;
        int j = 0;

		for(int i = 1; i <= bitList.size(); i++) {
            temp = temp << 1;
            temp = temp | bitList.get(i - 1);
            if(i % 8 == 0) {
                Log.d(InterpretationTask.LOG_TAG, Integer.toHexString(temp));
                bytes[j++] = (byte) temp;
                temp = 0;
            }
        }
        return bytes;
    }

    /**
     *
     * @return
     */
    protected abstract List<Byte> interpretMessage();

    @Override
    public void run() {
        this.smoothData();
        this.calculateThresholds();
		List<Byte> bitList = this.interpretMessage();
		if(bitList.size() < (40 + this.preamble.length)) {
			Log.d(InterpretationTask.LOG_TAG, "Received message is to small");
			return;
		}
        if(this.checkPreamble(bitList)) {
			this.message.setDataBytes(this.mergeBitsToBytes(bitList));
        } else {
			this.message.setMessageState(Message.MessageState.INVALID_PREAMBLE);
		}
		this.receiver.callback(this.message);
    }
}
