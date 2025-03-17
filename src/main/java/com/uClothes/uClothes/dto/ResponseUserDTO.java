package com.uClothes.uClothes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uClothes.uClothes.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUserDTO {
    private boolean isSuccess;
    private UUID id;
    private UserRole userRole;
    private String error, jwtToken, user, userEmail;

    public ResponseUserDTO(boolean isSuccess, String error) {
        this.isSuccess = isSuccess;
        this.error = error;
    }

    public ResponseUserDTO(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ResponseUserDTO(boolean isSuccess, UserRole userRole, String userEmail){
        this.isSuccess = isSuccess;
        this.userRole = userRole;
        this.userEmail = userEmail;
    }

    public ResponseUserDTO(boolean isSuccess, UserRole userRole, String user, String jwtToken) {
        this.isSuccess = isSuccess;
        this.userRole = userRole;
        this.user = user;
        this.jwtToken = jwtToken;
    }
}
