package com.cartify.authservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple POJO for auth users. We use JDBC.
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String mfaSecret;
    private String status;
}
