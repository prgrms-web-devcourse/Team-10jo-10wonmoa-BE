package com.prgrms.tenwonmoa.domain.accountbook.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.repository.SearchAccountBookRepository;
import com.prgrms.tenwonmoa.domain.category.CategoryType;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchAccountBookService {

	private final SearchAccountBookRepository repository;

	public FindAccountBookResponse<AccountBookItem> searchAccountBooks(Long authenticatedId, SearchAccountBookCmd cmd,
		PageCustomRequest pageRequest) {

		List<AccountBookItem> accountBookItems = repository.searchAccountBook(cmd.getMinPrice(), cmd.getMaxPrice(),
			cmd.getStart(), cmd.getEnd(), cmd.getContent(), cmd.getCategories(), authenticatedId, pageRequest);

		return calculateSum(accountBookItems, pageRequest);
	}

	private FindAccountBookResponse<AccountBookItem> calculateSum(List<AccountBookItem> results,
		PageCustomRequest pageRequest) {
		Long incomeSum = 0L;
		Long expenditureSum = 0L;

		for (AccountBookItem item : results) {
			if (item.getType().equals(CategoryType.INCOME.name())) {
				incomeSum += item.getAmount();
			} else {
				expenditureSum += item.getAmount();
			}
		}

		return FindAccountBookResponse.of(pageRequest, results, incomeSum, expenditureSum);
	}

}
