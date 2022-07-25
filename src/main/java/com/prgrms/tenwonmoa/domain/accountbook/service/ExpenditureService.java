package com.prgrms.tenwonmoa.domain.accountbook.service;

import org.springframework.stereotype.Service;

import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureRequest;
import com.prgrms.tenwonmoa.domain.accountbook.dto.CreateExpenditureResponse;
import com.prgrms.tenwonmoa.domain.accountbook.repository.ExpenditureRepository;
import com.prgrms.tenwonmoa.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenditureService {

	private final UserRepository userRepository;
	private final ExpenditureRepository expenditureRepository;

	public CreateExpenditureResponse createExpenditure(CreateExpenditureRequest createExpenditureRequest) {

	}
}