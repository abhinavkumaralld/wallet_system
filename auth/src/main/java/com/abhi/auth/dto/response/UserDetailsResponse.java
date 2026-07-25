package com.abhi.auth.dto.response;

import com.abhi.auth.common.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse {

    private  Long id;

    private String name;

    private String email;

    private Role role;

    private LocalDateTime createdAt;
}
