package Info;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.horizen.box.data.RegularBoxData;
import com.horizen.proposition.PublicKey25519Proposition;
import com.horizen.proposition.PublicKey25519PropositionSerializer;
import com.horizen.utils.BytesUtils;

import java.util.Arrays;

public class SendCoinInfo {

    private final long amount;
    private final PublicKey25519Proposition receiverAddress;

    public SendCoinInfo(long amount, PublicKey25519Proposition receiverAddress) {
        this.amount = amount;
        this.receiverAddress = receiverAddress;
    }

    public RegularBoxData getPaymentBoxData() {
        return new RegularBoxData(
                receiverAddress,
                amount
        );
    }

    public byte[] bytes() {
        byte[] receiverAddressBytes = PublicKey25519PropositionSerializer.getSerializer().toBytes(receiverAddress);

        return Bytes.concat(
                Longs.toByteArray(amount),
                Ints.toByteArray(receiverAddressBytes.length),
                receiverAddressBytes
        );
    }

    // Define object deserialization similar to 'toBytes()' representation.
    public static SendCoinInfo parseBytes(byte[] bytes) {
        int offset = 0;

        int batchSize = BytesUtils.getInt(bytes, offset);

        long amount = BytesUtils.getLong(bytes, offset);
        offset += 8;

        batchSize = BytesUtils.getInt(bytes, offset);
        offset += 4;

        PublicKey25519Proposition receiverAddress = PublicKey25519PropositionSerializer.getSerializer()
                .parseBytes(Arrays.copyOfRange(bytes, offset, offset + batchSize));

        return new SendCoinInfo(amount, receiverAddress);
    }
}
