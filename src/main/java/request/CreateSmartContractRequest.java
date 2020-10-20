package request;

/*
 Customized smart contract request implemented by Khoa.T.Truong
 */
public class CreateSmartContractRequest {
    public String returnAddress;
    public String message;

    public void setReturnAddress(String returnAddress){
        this.returnAddress = returnAddress;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
