package com.prgrms.tenwonmoa.domain.accountbook.dto.condition;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

/**
 * now: 애플리케이션상 현재 시각
 * conditionYear: 클라이언트가 입력한 요청한 년도
 * isFuture : 요청받은 년도가 미래인지
 * isPast : 요청받은 년도가 과거인지
 * **/
@Getter
public class MonthCondition {

	private static final List<Integer> allMonth = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

	private final LocalDateTime now;

	private final int conditionYear;

	private final boolean isFuture;

	private final boolean isPast;

	public MonthCondition(LocalDateTime now, int conditionYear) {
		this.now = now;
		this.conditionYear = conditionYear;
		this.isFuture = getNowYear() < conditionYear;
		this.isPast = getNowYear() > conditionYear;
	}

	/**
	 * 과거면 1 ~ 12월 작성한 것이 없더라도 모두 보여주어야 한다
	 * 현재면 미래 월을작성했다면 미래월부터 1월까지
	 * 현재면 현재월 이하로 작성되었다면 현재월 ~ 1월까지
	 * **/
	public Set<Integer> getNotFutureMonthSet(Set<Integer> monthSet) {
		Set<Integer> set = new HashSet<>();
		set.addAll(monthSet);

		if (isPast) {
			// 과거면 작성하든 하지 않든 모두 보여주어야 한다.
			set.addAll(allMonth);
			return set;
		}

		Integer maxMonth = monthSet.isEmpty() ? 0 : Collections.max(monthSet);
		int currMonth = getNowMonth();

		// 현재년도에서 작성된 월과 현재 해당월의 크기 비교
		int fromMonth = Math.max(currMonth, maxMonth);

		for (int i = 1; i <= fromMonth; i++) {
			set.add(i);
		}

		return set;
	}

	private int getNowYear() {
		return now.getYear();
	}

	private int getNowMonth() {
		return now.getMonthValue();
	}

}
