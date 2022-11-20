package ru.mirea.web3.web3javaclient.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.mirea.web3.web3javaclient.entity.User;

@Service
public class SecurityUserDetailsService implements UserDetailsService {
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = restTemplate.getForObject("/user?username=" + username, User.class);

        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        } else {
            return user;
        }
    }
}
