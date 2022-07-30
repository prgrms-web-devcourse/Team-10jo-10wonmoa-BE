package com.prgrms.tenwonmoa.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.prgrms.tenwonmoa.domain.accountbook.repository.AccountBookQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@TestConfiguration
public class TestConfig {

	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public AccountBookQueryRepository accountBookQueryRepository(JPAQueryFactory jpaQueryFactory) {
		return new AccountBookQueryRepository(jpaQueryFactory);
	}
}
