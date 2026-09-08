package com.aryan.razorpay.operations_service.settlement;

import com.aryan.razorpay.common_lib.entities.Money;
import com.aryan.razorpay.operations_service.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc);
}
