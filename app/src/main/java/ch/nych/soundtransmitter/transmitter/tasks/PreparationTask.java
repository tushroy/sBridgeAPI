package ch.nych.soundtransmitter.transmitter.tasks;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.message.Message;

/**
 * Created by nych on 4/6/16.
 */
public class PreparationTask extends TransmissionTask {

    public PreparationTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.PREPARATION_TASK);
    }
    private void twoStatePreparation() {
        System.out.println("ID:\t" + this.message.getMessageId() + " - prepare bytes");
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
        System.out.println("ID:\t" + this.message.getMessageId() + " - prepare bytes");
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
        if(this.transmitter.transmissionMode == Transmitter.TWO_STATE_TRANSMITTER){
            this.twoStatePreparation();
        } else if(this.transmitter.transmissionMode == Transmitter.FOUR_STATE_TRANSMITTER) {
            this.fourStatePreparation();
        } else {
            //Something really went wrong
            System.err.println("Take a look at the run method of the preparationtask");
        }
        this.transmitterCallback();
    }
}
