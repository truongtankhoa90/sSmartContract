package api;

import info.SendCoinInfo;
import akka.http.javadsl.server.Route;
import com.fasterxml.jackson.annotation.JsonView;
import com.horizen.api.http.ApiResponse;
import com.horizen.api.http.ApplicationApiGroup;
import com.horizen.api.http.ErrorResponse;
import com.horizen.api.http.SuccessResponse;
import com.horizen.box.Box;
import com.horizen.box.RegularBox;
import com.horizen.box.data.RegularBoxData;
import com.horizen.companion.SidechainTransactionsCompanion;
import com.horizen.node.NodeMemoryPool;
import com.horizen.node.SidechainNodeView;
import com.horizen.proof.Signature25519;
import com.horizen.proposition.Proposition;
import com.horizen.proposition.PublicKey25519Proposition;
import com.horizen.proposition.PublicKey25519PropositionSerializer;
import com.horizen.secret.Secret;
import com.horizen.serialization.Views;
import com.horizen.transaction.BoxTransaction;
import com.horizen.utils.ByteArrayWrapper;
import com.horizen.utils.BytesUtils;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import request.CreateSmartContractRequest;
import scala.Option;
import scala.Some;
import transaction.ReceiveCoinTransaction;

import java.util.*;

/*
 Customized API implemented by Khoa.T.Truong
 */
public class CustomSmartContractAPI extends ApplicationApiGroup {

    private final SidechainTransactionsCompanion sidechainTransactionsCompanion;

    public CustomSmartContractAPI(SidechainTransactionsCompanion sidechainTransactionsCompanion) {
        this.sidechainTransactionsCompanion = sidechainTransactionsCompanion;
    }

    @Override
    public String basePath() {
        return "smartC";    //acronym for smart contract
    }

    @Override
    public List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();
        routes.add(bindPostRequest("create", this::createSmartContract, CreateSmartContractRequest.class));
        return routes;
    }

    private ApiResponse createSmartContract(SidechainNodeView view, CreateSmartContractRequest ent) {
        try {
            if (ent.message != "ShowMoney") {
//                throw new IllegalStateException("Nothing to do. Invalid contract!");
            }
            // Parse the address (public key) of receiver.
            PublicKey25519Proposition receiverAddress = PublicKey25519PropositionSerializer.getSerializer()
                    .parseBytes(BytesUtils.fromHexString(ent.returnAddress));

            // Try to collect regular boxes to pay fee
            List<Box<Proposition>> paymentBoxes = new ArrayList<>();
            long amountToSend = 100000000; //send Coins in Zennies unit, assume 1 Zen once

            // Avoid to add boxes that are already spent in some Transaction that is present in node Mempool.
            List<byte[]> boxIdsToExclude = boxesFromMempool(view.getNodeMemoryPool());
            List<Box<Proposition>> regularBoxes = view.getNodeWallet().boxesOfType(RegularBox.class, boxIdsToExclude);
            int index = 0;
            while (amountToSend > 0 && index < regularBoxes.size()) {
                paymentBoxes.add(regularBoxes.get(index));
                amountToSend -= regularBoxes.get(index).value();
                index++;
            }

            if (amountToSend > 0) {
                throw new IllegalStateException("Not enough coins to send.");
            }

            // Set change if exists
            long change = Math.abs(amountToSend);
            List<RegularBoxData> regularOutputs = new ArrayList<>();
            if (change > 0) {
                regularOutputs.add(new RegularBoxData((PublicKey25519Proposition) paymentBoxes.get(0).proposition(), change));
            }

            // Create fake proofs to be able to create transaction to be signed.
            List<byte[]> inputRegularBoxIds = new ArrayList<>();
            for (Box b : paymentBoxes) {
                inputRegularBoxIds.add(b.id());
            }

            Long timestamp = System.currentTimeMillis();

            //send in Zennies unit (1Zen = 10^8 Zennies)
            SendCoinInfo sendCoinInfo = new SendCoinInfo(100000000, receiverAddress);
            List<Signature25519> fakeRegularInputProofs = Collections.nCopies(inputRegularBoxIds.size(), null);

            //accept the sent coins
            Optional<Secret> receiverSecretOption = view.getNodeWallet().secretByPublicKey(receiverAddress);
            if (!receiverSecretOption.isPresent()) {
//                return new SMCResponseError("0100", "Can't send coins because receiver proposition is not owned by the Node.", Option.empty());
            }

            ReceiveCoinTransaction unsignedTransaction = new ReceiveCoinTransaction(
                    inputRegularBoxIds,
                    fakeRegularInputProofs,
                    regularOutputs,
                    sendCoinInfo,
                    0,
                    timestamp
            );

            // Get the Tx message to be signed.
            byte[] messageToSign = unsignedTransaction.messageToSign();

            // Create regular signatures.
            List<Signature25519> regularInputProofs = new ArrayList<>();
            for (Box<Proposition> box : paymentBoxes) {
                regularInputProofs.add((Signature25519) view.getNodeWallet().secretByPublicKey(box.proposition()).get().sign(messageToSign));
            }

            ReceiveCoinTransaction transaction = new ReceiveCoinTransaction(
                    inputRegularBoxIds,
                    regularInputProofs,
                    regularOutputs,
                    sendCoinInfo,
                    0,
                    timestamp
            );
            return new TxResponse(ByteUtils.toHexString(sidechainTransactionsCompanion.toBytes((BoxTransaction) transaction)));
        }catch (Exception e) {
            return new SMCResponseError("0103", "Error during coin sent.", Some.apply(e));
        }
    }


    //customize a class for successful HTTP requests
    @JsonView(Views.Default.class)
    static class TxResponse implements SuccessResponse {
        public String response;
        public TxResponse(String response) {
            this.response = response;
        }
    }


    // customize a class for failed HTTP requests
    static class SMCResponseError implements ErrorResponse {
        private final String code;
        private final String description;
        private final Option<Throwable> exception;

        SMCResponseError(String code, String description, Option<Throwable> exception) {
            this.code = code;
            this.description = description;
            this.exception = exception;
        }

        @Override
        public String code() {
            return code;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public Option<Throwable> exception() {
            return exception;
        }
    }

    // Utility functions to get from the current mempool the list of all boxes to be opened.
    private List<byte[]> boxesFromMempool(NodeMemoryPool mempool) {
        List<byte[]> boxesFromMempool = new ArrayList<>();
        for(BoxTransaction tx : mempool.getTransactions()) {
            Set<ByteArrayWrapper> ids = tx.boxIdsToOpen();
            for(ByteArrayWrapper id : ids) {
                boxesFromMempool.add(id.data());
            }
        }
        return boxesFromMempool;
    }
}
