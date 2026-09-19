package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.security.neww;

import codeChallenge.chatgpt.eCommon.UserDto;

public class UserDtoBuilder {
    private Long id;
    private String name;
    private String email;

    public static UserDtoBuilder anInstance() {
        return new UserDtoBuilder();
    }

    public UserDto build() {
        UserDto userDto = new UserDto();
        userDto.setId(this.id);
        userDto.setName(this.name);
        userDto.setEmail(this.email);
        return userDto;
    }

    public UserDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserDtoBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserDtoBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
}
