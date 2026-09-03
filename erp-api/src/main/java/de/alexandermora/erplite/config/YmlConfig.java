package de.alexandermora.erplite.config;

import de.alexandermora.erplite.infrastructure.rest.customer.model.JsonPlaceHolderConfigModel;
import de.alexandermora.erplite.infrastructure.aws.model.AwsConfigModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties({AwsConfigModel.class, JsonPlaceHolderConfigModel.class})
@PropertySource(value = "classpath:aws/aws.yml", factory = YamlPropertySourceFactory.class)
@PropertySource(value = "classpath:jsonplaceholder/jsonplaceholder.yml", factory = YamlPropertySourceFactory.class)
public class YmlConfig {

}