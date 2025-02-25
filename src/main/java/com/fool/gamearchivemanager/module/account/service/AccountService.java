package com.fool.gamearchivemanager.module.account.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fool.gamearchivemanager.config.cache.CacheTemplate;
import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import com.fool.gamearchivemanager.entity.po.Account;
import com.fool.gamearchivemanager.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class AccountService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    private final CacheTemplate cacheTemplate;

    public AccountService(PasswordEncoder passwordEncoder, AccountMapper accountMapper, CacheTemplate cacheTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.accountMapper = accountMapper;
        this.cacheTemplate = cacheTemplate;
    }


    public AccountDTO getUser(String username) {
        AccountDTO userinfo = (AccountDTO) cacheTemplate.get(username);
        if (userinfo != null) {
            return userinfo;
        }
        log.info("load user(username: {}) from database", username);
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getUsername, username);
        Account account = accountMapper.selectOne(wrapper);
        if (account == null) {
            return null;
        }

        AccountDTO dto = new AccountDTO(account.getId(), account.getUsername(), account.getPassword());
        cacheTemplate.put(username, dto);
        return dto;
    }

    public void add(AccountDTO dto) {
        Account account = new Account(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
        accountMapper.insert(account);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDTO account = getUser(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                return account.getPassword();
            }

            @Override
            public String getUsername() {
                return account.getUsername();
            }
        };

    }
}
