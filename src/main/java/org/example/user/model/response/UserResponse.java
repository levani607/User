package org.example.user.model.response;

import lombok.Data;
import org.example.user.model.domain.ApplicationUser;
import org.example.user.model.enums.UserRole;
import org.example.user.model.enums.UserStatus;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstname;
    private UserStatus status;
    private UserRole role;

    public UserResponse(ApplicationUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.status = user.getStatus();
        this.role = user.getRole();
    }
}
