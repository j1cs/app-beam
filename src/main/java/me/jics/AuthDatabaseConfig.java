package me.jics;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ConfigurationProperties("database")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDatabaseConfig implements Serializable {
    String url;
    String username;
    String password;
}