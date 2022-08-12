package com.prgrms.tenwonmoa.domain.common.page;

import java.util.List;

public interface PageResponse<T> {

	int getCurrentPage();

	Integer getNextPage();

	int getTotalPages();

	long getTotalElements();

	List<T> getResults();

}
