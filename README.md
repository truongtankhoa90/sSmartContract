# sSmartContract

Let us try to create a simple contact by following HTTP POS request:
curl -X POST "http://127.0.0.1:9085/smartC/create" -H "accept: application/json" -H "Content-Type:application/json" 
-d "{\"returnAddress\":\"08fcd8e18b23d6ff5efc8091007f118ebf205a03fecb265b24568e9548e5da8c\", \"message\":\"ShowMoney\"}"
