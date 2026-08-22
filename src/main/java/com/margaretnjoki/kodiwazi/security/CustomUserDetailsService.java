package com.margaretnjoki.kodiwazi.security;

import com.margaretnjoki.kodiwazi.entity.Contributor;
import com.margaretnjoki.kodiwazi.repository.ContributorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ContributorRepository contributorRepository;

    public CustomUserDetailsService(ContributorRepository contributorRepository) {
        this.contributorRepository = contributorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Contributor contributor = contributorRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No contributor found with email: " + email)
                );

        return User.builder()
                .username(contributor.getEmail())
                .password(contributor.getPassword())
                .disabled(!contributor.isEnabled())
                .authorities(Collections.emptyList())
                .build();
    }
}