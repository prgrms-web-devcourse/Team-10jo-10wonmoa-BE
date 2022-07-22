package com.prgrms.tenwonmoa.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.tenwonmoa.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
