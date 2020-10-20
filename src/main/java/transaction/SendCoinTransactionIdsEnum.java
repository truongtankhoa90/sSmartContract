package transaction;

public enum SendCoinTransactionIdsEnum {

    SendCoinTransactionId((byte)1),
    ReceiveCoinTransactionId((byte)2);

    private final byte id;

    SendCoinTransactionIdsEnum(byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }
}
