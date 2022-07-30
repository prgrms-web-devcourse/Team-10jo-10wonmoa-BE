package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountDayResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.PageCustomImpl;
import com.prgrms.tenwonmoa.domain.accountbook.dto.PageCustomRequest;
import com.prgrms.tenwonmoa.domain.accountbook.repository.AccountBookQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountBookQueryService {

	private final AccountBookQueryRepository accountBookQueryRepository;

	public PageCustomImpl<FindAccountDayResponse> findDailyAccount(PageCustomRequest pageRequest, int month) {
		return accountBookQueryRepository.findDailyAccount(pageRequest, month);
	}
}
