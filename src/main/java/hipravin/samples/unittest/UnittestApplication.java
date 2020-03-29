package hipravin.samples.unittest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class UnittestApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnittestApplication.class, args);
    }

}
