package fr.esgi.grp9.uparserbackend.user.domain;

import fr.esgi.grp9.uparserbackend.authentication.login.Role;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Field(value = "firstname")
    private String firstName;
    @Field(value = "lastname")
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private String password;
    @Field(value = "create_date")
    private Date createDate;
    @Field(value = "close_date")
    private Date closeDate;
    @Field(value = "last_login_date")
    private Date lastLoginDate;
    @DBRef
    private Set<Role> roles;
}