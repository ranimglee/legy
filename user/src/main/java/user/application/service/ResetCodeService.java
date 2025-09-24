package user.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class ResetCodeService {

    @Qualifier("resetcode")

    private final StringRedisTemplate redisTemplate;
    private static final SecureRandom secureRandom = new SecureRandom();

    private static final long EXPIRATION_MINUTES = 1;

    public ResetCodeService(    @Qualifier("resetcode")
                                StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public String generateCode(String email) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000)); // 000000-999999
        redisTemplate.opsForValue().set("RESET_CODE:" + email, code, Duration.ofMinutes(EXPIRATION_MINUTES));
        return code;
    }
    public boolean validateCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("RESET_CODE:" + email);
        return storedCode != null && storedCode.equals(code);
    }

    public void deleteCode(String email) {
        redisTemplate.delete("RESET_CODE:" + email);
    }
}
