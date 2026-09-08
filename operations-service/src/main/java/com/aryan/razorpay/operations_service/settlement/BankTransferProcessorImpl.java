package com.aryan.razorpay.operations_service.settlement;

import com.aryan.razorpay.common_lib.entities.Money;
import com.aryan.razorpay.common_lib.util.RandomizedUtil;
import com.aryan.razorpay.operations_service.settlement.dto.BankTransferResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BankTransferProcessorImpl implements BankTransferProcessor {
    @Override
    public BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc) {
        // call the bank API
        String registrationRef = "TXN_"+ RandomizedUtil.randomBase64(12);
        log.debug("Bank transfer call completed for settlementId: {}, registrationRef: {}", settlementId, registrationRef);
        return new BankTransferResult(registrationRef);
    }
}
