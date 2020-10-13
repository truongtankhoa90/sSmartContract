import com.google.inject.Guice;
import com.google.inject.Injector;
import com.horizen.SidechainApp;

import java.io.File;

/*
main class: SmartContract implemented by Khoa.T.Truong
 */
public class SmartContractApp {
    public static void main(String[] args){
        if (args.length == 0) {
            System.out.println("Please provide settings file name as first parameter!");
            return;
        }

        if (!new File(args[0]).exists()) {
            System.out.println("File on path " + args[0] + " doesn't exist");
            return;
        }

        String settingsFileName = args[0];

        Injector injector = Guice.createInjector(new SmartContractModule(settingsFileName));
        SidechainApp sidechainApp = injector.getInstance(SidechainApp.class);

        sidechainApp.run();
        System.out.println("Simple Smart Contract application successfully started...");
    }
}
