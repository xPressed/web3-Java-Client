package ru.mirea.web3.web3javaclient.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Collection;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @NonNull
    @NotEmpty(message = "Username can not be empty!")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$", message = "Only numbers and at least 3 and not more than 15 letters!")
    private String username;

    @NonNull
    @NotEmpty(message = "Password can not be empty!")
    private String password;

    private String repeated;

    private String old;

    private byte[] profilePicture;

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
