package com.prgrms.tenwonmoa.domain.accountbook.service;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.tenwonmoa.domain.accountbook.dto.AccountBookItem;
import com.prgrms.tenwonmoa.domain.accountbook.dto.FindAccountBookResponse;
import com.prgrms.tenwonmoa.domain.accountbook.dto.service.SearchAccountBookCmd;
import com.prgrms.tenwonmoa.domain.accountbook.repository.SearchAccountBookRepository;
import com.prgrms.tenwonmoa.domain.common.page.PageCustomRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchAccountBookService {

	private final SearchAccountBookRepository repository;

	public FindAccountBookResponse searchAccountBooks(Long authenticatedId, SearchAccountBookCmd cmd,
		PageCustomRequest pageRequest) {

		checkArgument(cmd.getMinPrice() <= cmd.getMaxPrice(), "최소값은 최대값 보다 작아야 합니다");
		checkArgument(cmd.getStart().compareTo(cmd.getEnd()) <= 0, "시작일은 종료일 전이여야 합니다");

		List<AccountBookItem> accountBookItems = repository.searchAccountBook(cmd.getMinPrice(), cmd.getMaxPrice(),
			cmd.getStart(), cmd.getEnd(), cmd.getContent(), cmd.getCategories(), authenticatedId, pageRequest);

		return FindAccountBookResponse.of(pageRequest, accountBookItems);
	}

}
