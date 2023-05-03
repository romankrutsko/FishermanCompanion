package com.fisherman.companion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseStatus {
    LOGGED_IN_SUCCESSFULLY("Logged in successfully"),
    LOGGED_OUT_SUCCESSFULLY("Logged out successfully"),
    USER_CREATED_SUCCESSFULLY("User created successfully"),
    PASSWORD_CHANGED_SUCCESSFULLY("Password changed successfully"),
    USERNAME_CHANGED_SUCCESSFULLY("Username changed successfully"),
    USER_DELETED_SUCCESSFULLY("User was deleted successfully"),
    UNAUTHORIZED("User is not authorized"),
    TOKEN_IS_INVALID("Token is invalid"),
    WRONG_CREDENTIALS("Login failed, wrong username or password"),
    EMAIL_IS_TAKEN("Given email is already taken"),
    USERNAME_IS_TAKEN("Given username is already taken"),
    PASSWORD_CANNOT_BE_SAME("Given password is same as current, please try another one"),
    USER_CANNOT_BE_FOUND("User cannot be found");

    private final String code;
}
