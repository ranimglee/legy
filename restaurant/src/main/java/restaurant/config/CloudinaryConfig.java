package restaurant.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dxzlkaxsh",
                "api_key", "774772473632565",
                "api_secret", "vsMclbf7FpSG5GJsuiBMEJwZzMs"
        ));
    }
}
