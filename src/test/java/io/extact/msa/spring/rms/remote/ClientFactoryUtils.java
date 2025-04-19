package io.extact.msa.spring.rms.remote;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import io.extact.msa.spring.platform.core.auth.client.LoginUserHeaderRequestInitializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.ErrorMessageDeserializer;
import io.extact.msa.spring.platform.fw.infrastructure.external.ExternalProperties;
import io.extact.msa.spring.platform.fw.infrastructure.external.RestClientErrorHandler;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigConversionServiceBuilder;
import io.extact.msa.spring.platform.fw.infrastructure.external.converter.ConfigMessageConveterBuilder;
import io.extact.msa.spring.test.spring.LocalHostUriBuilderFactory;

public class ClientFactoryUtils {

     static <T> T createClient(ExternalProperties prop, Environment env, Class<T> clazz) {

        HttpMessageConverter<Object> converter = ConfigMessageConveterBuilder
                .builder(prop)
                .build();
        ConversionService conversionService = ConfigConversionServiceBuilder
                .builder(prop)
                .build();

        RestClient restClient = RestClient.builder()
                .uriBuilderFactory(new LocalHostUriBuilderFactory(env))
                .messageConverters(converters -> converters.addFirst(converter))
                .defaultStatusHandler(new RestClientErrorHandler(new ErrorMessageDeserializer()))
                .requestInitializer(new LoginUserHeaderRequestInitializer())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .conversionService(conversionService)
                .build();
        return factory.createClient(clazz);
    }

}
