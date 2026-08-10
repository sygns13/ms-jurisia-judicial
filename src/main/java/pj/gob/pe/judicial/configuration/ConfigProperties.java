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

    @Value("${api.consultaia.post.processgemini.path}")
    private String pathProcessDocumentGemini;

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

    // --- Proxy ANTERIOR (general SIJ) ---
    @Value("${sij.proxy.config.enabled:false}")
    private Boolean proxyEnabled;

    @Value("${sij.proxy.config.host}")
    private String proxyURL;

    @Value("${sij.proxy.config.port}")
    private Integer proxyPort;

    // --- Proxy NUEVO (PAC ADcsjan → proxycsjan(2).pj.gob.pe:3128).
    //     Usado por ApiChatBotServiceImpl para el egress a Internet. ---
    @Value("${sij.proxy.google.enabled:false}")
    private Boolean proxyGoogleEnabled;

    @Value("${sij.proxy.google.host:}")
    private String proxyGoogleHost;

    @Value("${sij.proxy.google.port:0}")
    private Integer proxyGooglePort;
}
