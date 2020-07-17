package com.app2.engine.soap;

import com.calculator.wsdl.Add;
import com.calculator.wsdl.AddResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class CalculatorClient extends WebServiceGatewaySupport implements WebServiceMessageCallback {



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

    public void doWithMessage(WebServiceMessage message) {
        try {
            SOAPMessage soapMessage = ((SaajSoapMessage)message).getSaajMessage();
            SOAPHeader header = soapMessage.getSOAPHeader();
            SOAPHeaderElement security = header.addHeaderElement(new QName("http://schemas.xmlsoap.org/ws/2003/06/secext", "Security", "wsse"));
            SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
            SOAPElement username = usernameToken.addChildElement("Username", "wsse");
            SOAPElement password = usernameToken.addChildElement("Password", "wsse");
            username.setTextContent("X");
            password.setTextContent("Y");
        } catch (Exception e) {
            //... handle appropriately
        }
    }
}
