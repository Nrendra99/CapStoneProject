package org.user.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/** 
 * DTO class for user authentication requests containing email and password.
 */
public class AuthRequest {
    private String email;
    private String password;
}
