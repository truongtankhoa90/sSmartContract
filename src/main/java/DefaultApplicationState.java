import com.horizen.block.SidechainBlock;
import com.horizen.box.Box;
import com.horizen.proposition.Proposition;
import com.horizen.state.ApplicationState;
import com.horizen.state.SidechainStateReader;
import com.horizen.transaction.BoxTransaction;
import scala.util.Success;
import scala.util.Try;

import java.util.List;

public class DefaultApplicationState implements ApplicationState {
    @Override
    public boolean validate(SidechainStateReader sidechainStateReader, SidechainBlock sidechainBlock) {
        return true;
    }

    @Override
    public boolean validate(SidechainStateReader sidechainStateReader, BoxTransaction<Proposition, Box<Proposition>> boxTransaction) {
        return true;
    }

    @Override
    public Try<ApplicationState> onApplyChanges(SidechainStateReader sidechainStateReader, byte[] bytes, List<Box<Proposition>> list, List<byte[]> list1) {
        return new Success<>(this);
    }

    @Override
    public Try<ApplicationState> onRollback(byte[] bytes) {
        return new Success<>(this);
    }
}
