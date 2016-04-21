package ch.nych.soundtransmitter.transmitter.tasks.preparation;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.ModulationTask;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/6/16.
 */
public class PreparationTask extends TransmissionTask {

    public PreparationTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.PREPARATION_TASK);
    }
    private void twoStatePreparation() {
        short mask = 0;
        int i = 0;
        byte[] originalData = this.message.getOriginalData();
        byte[] preparedData = new byte[this.message.getOriginalData().length * 8];

        for(byte b : originalData) {
            mask = 0b0000000010000000;
            for(int j = 0; j < 8; j++) {
                if((b & mask) > 0){
                    preparedData[i++] = 1;
                } else {
                    preparedData[i++] = 0;
                }
                mask = (byte) (mask >> 1);
            }
        }
        this.message.setPreparedData(preparedData);
    }

    private void fourStatePreparation() {
        short mask = 0;
        int i = 0;
        byte[] originalData = this.message.getOriginalData();
        byte[] preparedData = new byte[originalData.length * 4];

        for(byte b : originalData) {
            mask = 0b0000000011000000;
            for(int j = 0; j < 4; j++) {
                preparedData[i] = (byte)(mask & b);
                preparedData[i] = (byte) (preparedData[i] >> (6 - (j * 2)));
                mask = (byte) (mask >> 2);
                i++;
            }
        }
        this.message.setPreparedData(preparedData);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return new ModulationTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        if(this.transmitter.getConfiguration().getTransmissionMode() ==
                Configuration.TWO_STATE_TRANSMISSION){
            this.twoStatePreparation();
        } else if(this.transmitter.getConfiguration().getTransmissionMode() ==
                Configuration.FOUR_STATE_TRANSMISSION) {
            this.fourStatePreparation();
        } else {
            //// TODO: 4/12/16
        }
        this.transmitterCallback();
    }
}
