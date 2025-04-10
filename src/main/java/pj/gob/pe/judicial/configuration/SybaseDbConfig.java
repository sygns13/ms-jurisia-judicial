package pj.gob.pe.judicial.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "pj.gob.pe.repository.sybase",
        entityManagerFactoryRef = "sybaseEntityManagerFactory",
        transactionManagerRef = "sybaseTransactionManager"
)
public class SybaseDbConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.sybase")
    public DataSource sybaseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean sybaseEntityManagerFactory(
            EntityManagerFactoryBuilder builder, Environment env) {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", env.getProperty("spring.datasource.sybase.hibernate.dialect"));
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.show_sql", true);

        return builder
                .dataSource(sybaseDataSource())
                .packages("pj.gob.pe.model.entities.sybase")
                .persistenceUnit("sybase")
                .properties(props)
                .build();
    }

    @Bean
    public PlatformTransactionManager sybaseTransactionManager(
            @Qualifier("sybaseEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
