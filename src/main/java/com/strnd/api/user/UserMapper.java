package com.strnd.api.user;

import com.strnd.api.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

    // username으로 유저 조회
    Optional<User> findByUsername(String username);
}
