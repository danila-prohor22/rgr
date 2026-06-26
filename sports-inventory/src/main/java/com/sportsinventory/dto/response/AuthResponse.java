package com.sportsinventory.dto.response;

import com.sportsinventory.entity.User;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private User.Role role;
}
