package transaction;

import info.SendCoinInfo;
import com.horizen.box.NoncedBox;
import com.horizen.box.data.RegularBoxData;
import com.horizen.proof.Signature25519;
import com.horizen.proposition.Proposition;
import scorex.core.serialization.BytesSerializable;
import scorex.core.serialization.ScorexSerializer;

import java.util.List;

import static transaction.SendCoinTransactionIdsEnum.SendCoinTransactionId;

public class SendCoinTransaction extends AbstractRegularTransaction {

    private List<NoncedBox<Proposition>> newBoxes;

    public SendCoinTransaction(List<byte[]> inputRegularBoxIds,
                               List<Signature25519> inputRegularBoxProofs,
                               List<RegularBoxData> outputRegularBoxesData,
                               SendCoinInfo sendCoinInfo,
                               long amount,
                               long timestamp)
    {
        super(inputRegularBoxIds, inputRegularBoxProofs, outputRegularBoxesData, amount, timestamp);
    }



    @Override
    public byte transactionTypeId() {
        return SendCoinTransactionId.id();
    }

    @Override
    public ScorexSerializer<BytesSerializable> serializer() {
        return null;
    }
}
