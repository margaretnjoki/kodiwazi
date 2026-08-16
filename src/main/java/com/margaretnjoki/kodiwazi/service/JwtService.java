package com.margaretnjoki.kodiwazi.service;


import lombok.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

        private final String secret;

        public JwtService(@Value("${jwt.secret}") String secret) {
            this.secret = secret;
        }

        public String generateToken(String email) {
            // JWT generation will go here
            return "";
        }
}
