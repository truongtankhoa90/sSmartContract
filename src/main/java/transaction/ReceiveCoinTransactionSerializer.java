package transaction;

import com.horizen.transaction.TransactionSerializer;
import scorex.util.serialization.Reader;
import scorex.util.serialization.Writer;

public final class ReceiveCoinTransactionSerializer implements TransactionSerializer<ReceiveCoinTransaction> {

    private static ReceiveCoinTransactionSerializer serializer = new ReceiveCoinTransactionSerializer();

    private ReceiveCoinTransactionSerializer() {
        super();
    }

    public static ReceiveCoinTransactionSerializer getSerializer() {
        return serializer;
    }

    @Override
    public void serialize(ReceiveCoinTransaction transaction, Writer writer) {
        writer.putBytes(transaction.bytes());
    }

    @Override
    public ReceiveCoinTransaction parse(Reader reader) {
        return null;
    }
}
