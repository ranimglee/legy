package user.domain.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TwoFactorAuthRepository {
    private final Map<String, String> userSecrets = new ConcurrentHashMap<>();

    public void saveSecret(String username, String secret) {
        userSecrets.put(username, secret);
    }

    public String getSecret(String username) {
        return userSecrets.get(username);
    }
}