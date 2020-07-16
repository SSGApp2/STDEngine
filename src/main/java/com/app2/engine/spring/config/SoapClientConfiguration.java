package com.app2.engine.spring.config;

import com.app2.engine.soap.CalculatorClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.HashMap;

@Configuration
public class SoapClientConfiguration {
    @Bean
    public Jaxb2Marshaller marshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.weather.wsdl", "com.calculator.wsdl");
        marshaller.setMarshallerProperties(new HashMap<String, Object>() {{
            put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
        }});
        return marshaller;
    }


    @Bean
    public CalculatorClient calculatorClient(Jaxb2Marshaller marshaller) {
        final CalculatorClient client = new CalculatorClient();
        client.setDefaultUri("http://tempuri.org");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

//    @Bean
//    public WeatherClient weatherClient(Jaxb2Marshaller marshaller) {
//        final WeatherClient client = new WeatherClient();
//        client.setDefaultUri("http://ws.cdyne.com/WeatherWS");
//        client.setMarshaller(marshaller);
//        client.setUnmarshaller(marshaller);
//        return client;
//    }

}
