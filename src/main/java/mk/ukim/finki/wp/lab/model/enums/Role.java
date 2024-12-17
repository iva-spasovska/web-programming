package mk.ukim.finki.wp.lab.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER, ROLE_ADMIN, ROLE_ADMIN2;

    @Override
    public String getAuthority() {
        return name();
    }
}