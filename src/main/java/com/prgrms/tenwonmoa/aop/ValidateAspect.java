package com.prgrms.tenwonmoa.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.service.IncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Aspect
public class ValidateAspect {
	private final IncomeService incomeService;

	@Before("@annotation(com.prgrms.tenwonmoa.aop.annotation.ValidateIncome) "
		+ "&& execution(* *(Long, Long, ..)) "
		+ "&& args(userId, incomeId, ..)")
	public void validateIncome(Long userId, Long incomeId) {
		log.debug("Validate Income ==> userId {}, incomeId : {}", userId, incomeId);
		Income income = incomeService.findById(incomeId);
		income.validateOwner(userId);
	}
}
