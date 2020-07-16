package com.app2.engine.soap;

import com.calculator.wsdl.Add;
import com.calculator.wsdl.AddResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class CalculatorClient extends WebServiceGatewaySupport {

    private static Logger LOGGER = LoggerFactory.getLogger(CalculatorClient.class);

    public AddResponse calcAdd(Integer a, Integer b){
        Add request = new Add();
        request.setIntA(a);
        request.setIntB(b);

        AddResponse response = (AddResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://www.dneonline.com/calculator.asmx", request,
                        new SoapActionCallback("http://tempuri.org/Add"));
        LOGGER.debug("Result = {}",response.getAddResult());
        return response;
    }

}
