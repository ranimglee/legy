package shared.port;

import shared.dto.UserInfo;

public interface UserQueryPort {
   // UserInfo getUserByEmail(String email);
    UserInfo getUserByUsername(String username);
    UserInfo getUserById(String userId);
   /* UserInfo getUserByFirstname(String firstname);
    UserInfo getUserByLastname(String lastname);*/
}