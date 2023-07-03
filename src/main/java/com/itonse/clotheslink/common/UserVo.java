package com.itonse.clotheslink.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserVo {
    private UserType userType;
    private Long id;
    private String email;
}