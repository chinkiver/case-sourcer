package com.lawfirm.caseledger.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lawfirm.caseledger.entity.SysUser;
import com.lawfirm.caseledger.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username", username).eq("deleted", 0));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> permissions = sysUserMapper.selectPermissionCodesByUserId(user.getId());
        return new SecurityUser(user, permissions);
    }
}
