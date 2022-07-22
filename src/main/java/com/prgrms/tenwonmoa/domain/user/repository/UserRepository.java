package com.prgrms.tenwonmoa.domain.user.repository;

import com.prgrms.tenwonmoa.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
