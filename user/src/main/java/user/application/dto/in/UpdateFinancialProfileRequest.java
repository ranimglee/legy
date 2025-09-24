package user.application.dto.in;

public record UpdateFinancialProfileRequest(
        String firstname,
        String lastname,
        String username,
        String phoneNumber,
        String rib) {

}
