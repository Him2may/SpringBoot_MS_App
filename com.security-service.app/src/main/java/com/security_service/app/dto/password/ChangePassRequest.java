package com.security_service.app.dto.password;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePassRequest {
    private String email;
    private String currentPassword;

    @NotNull(message = "Password should not be null")
    @Size(min = 6, message = "New Password must be at least 6 characters")
    @Size(max = 25, message = "New Password must have at max 18 characters")
    private String newPassword;


    @NotNull(message = "Confirm Password should not be null")
    @Size(min = 6, message = "Confirm Password must be at least 6 characters")
    @Size(max = 25, message = "Confirm Password must have at max 18 characters")
    private String confirmNewPassword;
}
