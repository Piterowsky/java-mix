package pl.piterowsky.javamix.pattern.functional;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.function.Consumer;

@Log4j2
public class FluentBuilder {

    public static void main(String[] args) {
        log.info("Imperative: {}", new UserImperativeBuilder(1L, "imperative")
                .postalCode("72-001")
                .email("imperative email")
                .build()
        );
        log.info("Functional: {}", new UserFunctionalBuilder(2L, "functional")
                .with(builder -> {
                    builder.email = "functional email";
                    builder.postalCode = "72-001";
                })
                .build()
        );
    }

}

class UserFunctionalBuilder {

    private final Long id;
    private final String login;

    public String email;
    public String postalCode;

    public UserFunctionalBuilder(Long id, String login) {
        this.id = id;
        this.login = login;
    }

    public UserFunctionalBuilder with(Consumer<UserFunctionalBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public User build() {
        var user = new User(id, login);
        user.setEmail(email);
        user.setEmail(postalCode);
        return user;
    }

}

class UserImperativeBuilder {

    private final Long id;
    private final String login;

    private String email;
    private String postalCode;

    public UserImperativeBuilder(Long id, String login) {
        this.id = id;
        this.login = login;
    }

    public UserImperativeBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserImperativeBuilder postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public User build() {
        var user = new User(id, login);
        user.setEmail(email);
        return user;
    }

}

@Data
class User {

    private final Long id;
    private final String login;
    private String email;

    public User(Long id, String login) {
        this.id = id;
        this.login = login;
    }

}

















