package fr.esgi.grp9.uparserbackend.authentication.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private String token;
}
