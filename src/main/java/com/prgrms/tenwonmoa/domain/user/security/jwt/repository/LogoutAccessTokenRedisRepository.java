package com.prgrms.tenwonmoa.domain.user.security.jwt.repository;

import org.springframework.data.repository.CrudRepository;

import com.prgrms.tenwonmoa.domain.user.security.jwt.LogoutAccessToken;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessToken, String> {
}
