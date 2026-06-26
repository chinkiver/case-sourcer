package com.lawfirm.caseledger.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lawfirm.caseledger.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SecurityUser implements UserDetails {

    private Long userId;
    private String username;
    @JsonIgnore
    private String password;
    private String realName;
    private String avatar;
    private Integer status;
    private Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(SysUser user, List<String> permissions) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.realName = user.getRealName();
        this.avatar = user.getAvatar();
        this.status = user.getStatus();
        this.authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
        return status != null && status == 1;
    }
}
