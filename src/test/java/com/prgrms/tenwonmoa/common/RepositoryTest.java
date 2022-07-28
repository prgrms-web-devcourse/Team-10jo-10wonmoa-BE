package com.prgrms.tenwonmoa.common;

import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.prgrms.tenwonmoa.common.annotation.CustomDataJpaTest;

@Disabled
@CustomDataJpaTest
public class RepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	protected <T> T save(T entity) {
		entityManager.persist(entity);
		return entity;
	}
}
