package com.prgrms.tenwonmoa.domain.accountbook;

import java.time.LocalDateTime;

public final class AccountBookConst {
	private AccountBookConst() {
	}

	public static final Long AMOUNT_MAX = 1000000000000L;
	public static final Long AMOUNT_MIN = 1L;
	public static final int CONTENT_MAX = 50;

	public static final LocalDateTime LEFT_MOST_REGISTER_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

	public static final LocalDateTime RIGHT_MOST_REGISTER_DATE = LocalDateTime.of(3000, 12, 31, 23, 59);
}
