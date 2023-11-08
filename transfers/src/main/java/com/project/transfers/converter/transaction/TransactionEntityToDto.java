package com.project.transfers.converter.transaction;

import com.project.transfers.core.dto.Transaction;
import com.project.transfers.repository.entity.TransactionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
@Component
public class TransactionEntityToDto implements Converter<TransactionEntity, Transaction> {

    @Override
    public Transaction convert(TransactionEntity source) {
        return Transaction.builder()
                .setId(source.getId())
                .setCurrency(source.getCurrency())
                .setDate(source.getDate())
                .setType(source.getType())
                .build();
    }
}
