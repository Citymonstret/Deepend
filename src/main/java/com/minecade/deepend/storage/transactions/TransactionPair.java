package com.minecade.deepend.storage.transactions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class TransactionPair {

    @Getter
    private final PendingTransaction pendingTransaction;

    @Getter
    private final TransactionResult transactionResult;
}