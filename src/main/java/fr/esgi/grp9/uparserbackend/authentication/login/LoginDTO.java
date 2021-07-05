package fr.esgi.grp9.uparserbackend.authentication.login;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class LoginDTO {

    @NonNull
    private String email;
    @NonNull
    private String password;
}
