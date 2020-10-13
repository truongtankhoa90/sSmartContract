import com.horizen.box.Box;
import com.horizen.proposition.Proposition;
import com.horizen.secret.Secret;
import com.horizen.wallet.ApplicationWallet;

import java.util.List;

public class DefaultApplicationWallet implements ApplicationWallet {
    @Override
    public void onAddSecret(Secret secret) {

    }

    @Override
    public void onRemoveSecret(Proposition proposition) {

    }

    @Override
    public void onChangeBoxes(byte[] bytes, List<Box<Proposition>> list, List<byte[]> list1) {

    }

    @Override
    public void onRollback(byte[] bytes) {

    }
}
