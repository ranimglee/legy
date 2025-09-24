package user.application.dto.out;

public record ShiftStatusNotification(
        String status,         // APPROVED or REJECTED
        String shiftId
) {}
