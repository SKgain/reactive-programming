package com.dhrubok.reactive_bank.constant;

import com.dhrubok.reactive_bank.entity.Account;

public final class AccountConstant {
    public static final String INITIAL_ACCOUNT_NUMBER = "ACC1001";
    public static final String ACCOUNT_CODE = "ACC";
    public static final String TRANSACTION_CODE = "TXN";
    public static final String INITIAL_TRANSACTION_NUMBER = "TXN121301";
    public static final String TRANSACTION_TYPE_TRANSFER = "transfer";
    public static final String TRANSACTION_TYPE_DEPOSIT = "deposit";
    public static final Account DEPOSIT_FROM = new Account("myself");
}
