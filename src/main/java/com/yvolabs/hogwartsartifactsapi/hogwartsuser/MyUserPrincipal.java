package com.yvolabs.hogwartsartifactsapi.hogwartsuser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Yvonne N
 * Think of this UserDetails interface as a contract between Developers & Spring Security.
 * Spring Security says I'm happy to authenticate users, but it's the developers reponsibilty
 * to encapsulate the returned user into a class that implements UserDetails.
 * Spring Security Does not know your e.g. HogwartsUser it only recognizes UserDetail.
 * <p>
 * The Authentication Provider will use the methods defined in UserDetails to obtain user info/credentials
 */
@Getter
@AllArgsConstructor
public class MyUserPrincipal implements UserDetails {

    private HogwartsUser hogwartsUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String[] roles = StringUtils.tokenizeToStringArray(this.hogwartsUser.getRoles(), " ");
        return Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

    }

    @Override
    public String getPassword() {
        return this.hogwartsUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.hogwartsUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.hogwartsUser.isEnabled();
    }
}
