package pj.gob.pe.judicial.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ConfigProperties {

    @Value("${api.consultaia.url}")
    private String urlConsultaia;

    @Value("${api.consultaia.post.process.path}")
    private String pathProcessDocument;

    @Value("${api.security.url}")
    private String urlSecurityAPI;

    @Value("${api.security.get.session.path}")
    private String pathGetSession;

    @Value("${spring.data.redis.prefix:jurisia_security}")
    private String REDIS_KEY_PREFIX;

    @Value("${spring.data.redis.ttl:3600}")
    private long REDIS_TTL;

    @Value("${api.apichatbot.url}")
    private String urlApiChatBot;

    @Value("${api.apichatbot.get.pending.path}")
    private String pathGetPendings;

    @Value("${api.apichatbot.post.info.path}")
    private String pathInfoExpedientes;

    @Value("${sij.proxy.config.enabled:false}")
    private Boolean proxyEnabled;

    @Value("${sij.proxy.config.host}")
    private String proxyURL;

    @Value("${sij.proxy.config.port}")
    private Integer proxyPort;
}
