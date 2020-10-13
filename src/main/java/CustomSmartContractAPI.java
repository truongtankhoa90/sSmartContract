import akka.http.javadsl.server.Route;
import com.fasterxml.jackson.annotation.JsonView;
import com.horizen.api.http.ApiResponse;
import com.horizen.api.http.ApplicationApiGroup;
import com.horizen.api.http.ErrorResponse;
import com.horizen.api.http.SuccessResponse;
import com.horizen.companion.SidechainTransactionsCompanion;
import com.horizen.node.SidechainNodeView;
import com.horizen.serialization.Views;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

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
        routes.add(bindPostRequest("creat", this::createSmartContract, CreateSmartContractRequest.class));
        return routes;
    }

    private ApiResponse createSmartContract(SidechainNodeView view, CreateSmartContractRequest ent) {
        if (ent.message == "Show me the money!"){

        }
        return null;
    }


    //customize a class for successful HTTP requests
    @JsonView(Views.Default.class)
    static class CustomSuccessResponse implements SuccessResponse {
        public String response;

        public CustomSuccessResponse(String response) {
            this.response = response;
        }
    }


    // customize a class for failed HTTP requests
    // based on docs
    static class CarResponseError implements ErrorResponse {
        private final String code;
        private final String description;
        private final Option<Throwable> exception;

        CarResponseError(String code, String description, Option<Throwable> exception) {
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
}
