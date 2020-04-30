package ${package}.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
public class ServerApplication {
    // swagger扫描接口的位置
    private static final String swaggerScanPackage = "${package}.server.controller";

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        // 生产和预发环境不开启swagger
        if (env.contains("prod") || env.contains("pre")) {
            return new Docket(DocumentationType.SWAGGER_2).groupName(appName).enable(false);
        }
        return new Docket(DocumentationType.SWAGGER_2).groupName(appName)
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerScanPackage))
                .build().apiInfo(ApiInfo.DEFAULT).useDefaultResponseMessages(false);
    }

}

