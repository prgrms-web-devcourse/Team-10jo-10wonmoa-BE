package com.prgrms.tenwonmoa.domain.accountbook.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountDayResponse;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.AccountBookQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountBookQueryService {

	private final AccountBookQueryRepository accountBookQueryRepository;

	public PageCustomImpl<FindAccountDayResponse> findDailyAccount(Long userId, PageCustomRequest pageRequest,
		LocalDateTime registeredMonth) {
		return accountBookQueryRepository.findDailyAccount(userId, pageRequest, registeredMonth);
	}
}
