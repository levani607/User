package org.example.user.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest extends UserBaseRequest{

    @NotEmpty(message = "username can not be empty!;")
    private String username;

}
