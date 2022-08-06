package com.prgrms.tenwonmoa.domain.accountbook.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.Expenditure;
import com.prgrms.tenwonmoa.domain.accountbook.Income;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse.Result;
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

	public FindAccountBookResponse searchAccountBooks(Long authenticatedId, SearchAccountBookCmd cmd,
		PageCustomRequest pageRequest) {
		List<Expenditure> expenditures = repository.searchExpenditures(cmd.getMinPrice(), cmd.getMaxPrice(),
			cmd.getStart(), cmd.getEnd(), cmd.getContent(), cmd.getCategories(), authenticatedId, pageRequest);

		List<Income> incomes = repository.searchIncomes(cmd.getMinPrice(), cmd.getMaxPrice(), cmd.getStart(),
			cmd.getEnd(), cmd.getContent(), cmd.getCategories(), authenticatedId, pageRequest);

		List<Result> results = combineAndSort(expenditures, incomes);

		return sliceAndSum(results, pageRequest);
	}

	private List<Result> combineAndSort(List<Expenditure> expenditures, List<Income> incomes) {
		List<Result> results = expenditures.stream()
			.map(e -> new Result(e.getDate(), e.getAmount(), e.getContent(), e.getId(),
				CategoryType.EXPENDITURE.name(), e.getCategoryName()))
			.collect(Collectors.toList());

		incomes.forEach(i ->
			results.add(new Result(i.getDate(), i.getAmount(), i.getContent(),
				i.getId(), CategoryType.INCOME.name(), i.getCategoryName())));

		results.sort(Comparator.comparing(Result::getRegisterDate, (date1, date2) -> (-1) * date1.compareTo(date2)));
		return results;
	}

	private FindAccountBookResponse sliceAndSum(List<Result> results, PageCustomRequest pageRequest) {
		List<Result> slicedResult;

		if (results.size() > pageRequest.getSize()) {
			slicedResult = new ArrayList<>(results.subList(0, pageRequest.getSize()));
		} else {
			slicedResult = new ArrayList<>(results);
		}

		Long incomeSum = 0L;
		Long expenditureSum = 0L;

		for (Result result : slicedResult) {
			if (result.getType().equals(CategoryType.INCOME.name())) {
				incomeSum += result.getAmount();
			} else {
				expenditureSum += result.getAmount();
			}
		}

		return FindAccountBookResponse.of(pageRequest, slicedResult, incomeSum, expenditureSum);
	}

}
