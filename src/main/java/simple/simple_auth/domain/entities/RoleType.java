package simple.simple_auth.domain.entities;

import lombok.Getter;

@Getter
public enum RoleType {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String name;

    RoleType(String name) {
        this.name = name;
    }
}
