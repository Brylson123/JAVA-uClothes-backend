package com.uClothes.uClothes.security;

import com.uClothes.uClothes.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {
    private String username, password, email;
    UserRole userRole;
}
