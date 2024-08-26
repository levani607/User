package org.example.user.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.user.model.enums.UserRole;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseRequest {
    @Size(min = 4,max = 32,message = "password size must be between 4-32;")
    @NotNull(message = "password can not be null!;")
    private String password;
    @NotEmpty(message = "username can not be empty!;")
    private String email;
    @NotEmpty(message = "firstname can not be empty!;")
    private String firstname;
    @NotNull(message = "role can not be null!;")
    private UserRole role;
}
