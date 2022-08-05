package com.prgrms.tenwonmoa.domain.accountbook;

import java.time.LocalDate;

public final class AccountBookConst {
	private AccountBookConst() {
	}

	public static final Long AMOUNT_MAX = 1000000000000L;
	public static final Long AMOUNT_MIN = 1L;
	public static final int CONTENT_MAX = 50;

	public static final LocalDate LEFT_MOST_REGISTER_DATE = LocalDate.of(2000, 1, 1);

	public static final LocalDate RIGHT_MOST_REGISTER_DATE = LocalDate.of(3000, 12, 31);
}
