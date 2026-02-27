package com.blackpearl.dto;

import com.blackpearl.model.User.Role;
import com.blackpearl.model.User.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Department department;
    private Role role;
    private boolean active;
}
