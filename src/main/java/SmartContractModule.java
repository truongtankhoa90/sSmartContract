import api.CustomSmartContractAPI;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.horizen.SidechainSettings;
import com.horizen.api.http.ApplicationApiGroup;
import com.horizen.box.Box;
import com.horizen.box.BoxSerializer;
import com.horizen.box.NoncedBox;
import com.horizen.box.data.NoncedBoxData;
import com.horizen.box.data.NoncedBoxDataSerializer;
import com.horizen.companion.SidechainBoxesDataCompanion;
import com.horizen.companion.SidechainProofsCompanion;
import com.horizen.companion.SidechainTransactionsCompanion;
import com.horizen.proof.Proof;
import com.horizen.proof.ProofSerializer;
import com.horizen.proposition.Proposition;
import com.horizen.secret.Secret;
import com.horizen.secret.SecretSerializer;
import com.horizen.settings.SettingsReader;
import com.horizen.state.ApplicationState;
import com.horizen.storage.IODBStorageUtil;
import com.horizen.storage.Storage;
import com.horizen.transaction.BoxTransaction;
import com.horizen.transaction.TransactionSerializer;
import com.horizen.utils.Pair;
import com.horizen.wallet.ApplicationWallet;
import transaction.ReceiveCoinTransactionSerializer;
import transaction.SendCoinTransactionIdsEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/*
SmartContractModule: implemented by Khoa.T.Truong
 */
public class SmartContractModule extends AbstractModule {
    private SettingsReader settingsReader;

    SmartContractModule(String userSettingsFileName){
        this.settingsReader = new SettingsReader(userSettingsFileName, Optional.empty());
    }

    @Override
    protected void configure() {

        SidechainSettings sidechainSettings = this.settingsReader.getSidechainSettings();

        HashMap<Byte, BoxSerializer<Box<Proposition>>> customBoxSerializers = new HashMap<>();
        HashMap<Byte, NoncedBoxDataSerializer<NoncedBoxData<Proposition, NoncedBox<Proposition>>>> customBoxDataSerializers = new HashMap<>();
        HashMap<Byte, SecretSerializer<Secret>> customSecretSerializers = new HashMap<>();
        HashMap<Byte, ProofSerializer<Proof<Proposition>>> customProofSerializers = new HashMap<>();
        HashMap<Byte, TransactionSerializer<BoxTransaction<Proposition, Box<Proposition>>>> customTransactionSerializers = new HashMap<>();

        ApplicationWallet defaultApplicationWallet = new DefaultApplicationWallet();
        ApplicationState defaultApplicationState = new DefaultApplicationState();

        File secretStore = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/secret");
        File walletBoxStore = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/wallet");
        File walletTransactionStore = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/walletTransaction");
        File walletForgingBoxesInfoStorage = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/walletForgingStake");
        File stateStore = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/state");
        File historyStore = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/history");
        File consensusStore = new File(sidechainSettings.scorexSettings().dataDir().getAbsolutePath() + "/consensusData");

        customTransactionSerializers.put(SendCoinTransactionIdsEnum.ReceiveCoinTransactionId.id(), (TransactionSerializer) ReceiveCoinTransactionSerializer.getSerializer());
        SidechainBoxesDataCompanion sidechainBoxesDataCompanion = new SidechainBoxesDataCompanion(customBoxDataSerializers);
        SidechainProofsCompanion sidechainProofsCompanion = new SidechainProofsCompanion(customProofSerializers);

        SidechainTransactionsCompanion transactionsCompanion = new SidechainTransactionsCompanion(customTransactionSerializers,
                sidechainBoxesDataCompanion, sidechainProofsCompanion);

        // Here I can add my custom rest api and/or override existing one
        List<ApplicationApiGroup> customApiGroups = new ArrayList<>();
        customApiGroups.add(new CustomSmartContractAPI(transactionsCompanion));

        List<Pair<String, String>> rejectedApiPaths = new ArrayList<>();



        bind(SidechainSettings.class)
                .annotatedWith(Names.named("SidechainSettings"))
                .toInstance(sidechainSettings);

        bind(new TypeLiteral<HashMap<Byte, BoxSerializer<Box<Proposition>>>>() {})
                .annotatedWith(Names.named("CustomBoxSerializers"))
                .toInstance(customBoxSerializers);
        bind(new TypeLiteral<HashMap<Byte, NoncedBoxDataSerializer<NoncedBoxData<Proposition, NoncedBox<Proposition>>>>>() {})
                .annotatedWith(Names.named("CustomBoxDataSerializers"))
                .toInstance(customBoxDataSerializers);
        bind(new TypeLiteral<HashMap<Byte, SecretSerializer<Secret>>>() {})
                .annotatedWith(Names.named("CustomSecretSerializers"))
                .toInstance(customSecretSerializers);
        bind(new TypeLiteral<HashMap<Byte, ProofSerializer<Proof<Proposition>>>>() {})
                .annotatedWith(Names.named("CustomProofSerializers"))
                .toInstance(customProofSerializers);
        bind(new TypeLiteral<HashMap<Byte, TransactionSerializer<BoxTransaction<Proposition, Box<Proposition>>>>>() {})
                .annotatedWith(Names.named("CustomTransactionSerializers"))
                .toInstance(customTransactionSerializers);

        bind(ApplicationWallet.class)
                .annotatedWith(Names.named("ApplicationWallet"))
                .toInstance(defaultApplicationWallet);

        bind(ApplicationState.class)
                .annotatedWith(Names.named("ApplicationState"))
                .toInstance(defaultApplicationState);

        bind(Storage.class)
                .annotatedWith(Names.named("SecretStorage"))
                .toInstance(IODBStorageUtil.getStorage(secretStore));
        bind(Storage.class)
                .annotatedWith(Names.named("WalletBoxStorage"))
                .toInstance(IODBStorageUtil.getStorage(walletBoxStore));
        bind(Storage.class)
                .annotatedWith(Names.named("WalletTransactionStorage"))
                .toInstance(IODBStorageUtil.getStorage(walletTransactionStore));
        bind(Storage.class)
                .annotatedWith(Names.named("WalletForgingBoxesInfoStorage"))
                .toInstance(IODBStorageUtil.getStorage(walletForgingBoxesInfoStorage));
        bind(Storage.class)
                .annotatedWith(Names.named("StateStorage"))
                .toInstance(IODBStorageUtil.getStorage(stateStore));
        bind(Storage.class)
                .annotatedWith(Names.named("HistoryStorage"))
                .toInstance(IODBStorageUtil.getStorage(historyStore));
        bind(Storage.class)
                .annotatedWith(Names.named("ConsensusStorage"))
                .toInstance(IODBStorageUtil.getStorage(consensusStore));

        bind(new TypeLiteral<List<ApplicationApiGroup>> () {})
                .annotatedWith(Names.named("CustomApiGroups"))
                .toInstance(customApiGroups);

        bind(new TypeLiteral<List<Pair<String, String>>> () {})
                .annotatedWith(Names.named("RejectedApiPaths"))
                .toInstance(rejectedApiPaths);
    }
}
