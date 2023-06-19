package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security;

import org.springframework.security.core.GrantedAuthority;

public class UserAuthorityRoles implements GrantedAuthority {
    @Override
    public String getAuthority() {
        return null;
    }
}
