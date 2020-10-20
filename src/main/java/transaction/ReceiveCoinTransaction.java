package transaction;

import Info.SendCoinInfo;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.horizen.box.NoncedBox;
import com.horizen.box.RegularBox;
import com.horizen.box.data.RegularBoxData;
import com.horizen.proof.Signature25519;
import com.horizen.proposition.Proposition;
import com.horizen.transaction.TransactionSerializer;
import scorex.core.serialization.BytesSerializable;
import scorex.core.serialization.ScorexSerializer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static transaction.SendCoinTransactionIdsEnum.SendCoinTransactionId;

public class ReceiveCoinTransaction extends AbstractRegularTransaction{

    private List<NoncedBox<Proposition>> newBoxes;

    private final SendCoinInfo sendCoinInfo;

    public ReceiveCoinTransaction(List<byte[]> inputRegularBoxIds,
                             List<Signature25519> inputRegularBoxProofs,
                             List<RegularBoxData> outputRegularBoxesData,
                             SendCoinInfo sendCoinInfo,
                             long fee,
                             long timestamp) {
        super(inputRegularBoxIds, inputRegularBoxProofs, outputRegularBoxesData, fee, timestamp);
        this.sendCoinInfo = sendCoinInfo;
    }


    @Override
    public byte transactionTypeId() {
        return SendCoinTransactionId.id();
    }

    @Override
    public TransactionSerializer serializer() {
        return ReceiveCoinTransactionSerializer.getSerializer();
    }

    @Override
    public List<NoncedBox<Proposition>> newBoxes() {
        if(newBoxes == null) {
            // Get new boxes from base class.
            newBoxes = new ArrayList<>(super.newBoxes());
            RegularBoxData paymentBoxData = sendCoinInfo.getPaymentBoxData();
            long nonce = getNewBoxNonce(paymentBoxData.proposition(), newBoxes.size());
            newBoxes.add((NoncedBox) new RegularBox(paymentBoxData, nonce));
        }
        return Collections.unmodifiableList(newBoxes);
    }

    // Define object serialization, that should serialize both parent class entries and CarBuyOrderInfo as well
    @Override
    public byte[] bytes() {
        ByteArrayOutputStream inputsIdsStream = new ByteArrayOutputStream();
        for(byte[] id: inputRegularBoxIds)
            inputsIdsStream.write(id, 0, id.length);

        byte[] inputRegularBoxIdsBytes = inputsIdsStream.toByteArray();

        byte[] inputRegularBoxProofsBytes = regularBoxProofsSerializer.toBytes(inputRegularBoxProofs);

        byte[] outputRegularBoxesDataBytes = regularBoxDataListSerializer.toBytes(outputRegularBoxesData);

        byte[] sendCoinInfoBytes = sendCoinInfo.bytes();

        return Bytes.concat(
                Longs.toByteArray(fee()),                               // 8 bytes
                Longs.toByteArray(timestamp()),                         // 8 bytes
                Ints.toByteArray(inputRegularBoxIdsBytes.length),       // 4 bytes
                inputRegularBoxIdsBytes,                                // depends on previous value (>=4 bytes)
                Ints.toByteArray(inputRegularBoxProofsBytes.length),    // 4 bytes
                inputRegularBoxProofsBytes,                             // depends on previous value (>=4 bytes)
                Ints.toByteArray(outputRegularBoxesDataBytes.length),   // 4 bytes
                outputRegularBoxesDataBytes,                            // depends on previous value (>=4 bytes)
                Ints.toByteArray(sendCoinInfoBytes.length),          // 4 bytes
                sendCoinInfoBytes                                    // depends on previous value (>=4 bytes)
        );
    }
}
