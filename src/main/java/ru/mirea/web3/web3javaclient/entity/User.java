package ru.mirea.web3.web3javaclient.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    private String token;
    @NonNull
    @NotEmpty(message = "Username can not be empty!")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$", message = "Only numbers and at least 3 and not more than 15 letters!")
    private String username;

    @NonNull
    @NotEmpty(message = "Password can not be empty!")
    private String password;

    private String repeated;

    @Size(max = 100, message = "Only numbers and not more than 100 letters!")
    private String description;

    private String old;

    private byte[] profilePicture;
    private List<Post> postList;
    private List<String> subs;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
