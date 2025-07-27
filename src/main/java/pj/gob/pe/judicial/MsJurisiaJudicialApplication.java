package pj.gob.pe.judicial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MsJurisiaJudicialApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsJurisiaJudicialApplication.class, args);
    }

}
