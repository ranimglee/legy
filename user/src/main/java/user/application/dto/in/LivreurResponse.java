package user.application.dto.in;


import user.domain.model.Status;

public record LivreurResponse(
        String id,
        String username,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        String address,
        String rib,
        String matricule,
        Boolean isAssigned,
        Status status,
        String role

) {}
