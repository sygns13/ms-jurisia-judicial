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

    @Value("${api.openai.secret.key}")
    private String secretKeyOpenAI;

    @Value("${spring.data.redis.prefix:jurisia_security}")
    private String REDIS_KEY_PREFIX;

    @Value("${spring.data.redis.ttl:3600}")
    private long REDIS_TTL;
}
