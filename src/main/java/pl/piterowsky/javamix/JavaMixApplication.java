package pl.piterowsky.javamix;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.piterowsky.javamix.non.blocking.io.NonBlockingIO;

@SpringBootApplication
public class JavaMixApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(JavaMixApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var nonBlockingIO = new NonBlockingIO();
        nonBlockingIO.start();
    }

}
