package com.abhi.wallet.repository;

import com.abhi.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    boolean existsByReferenceId(String referenceId);

    List<Transaction> findAllByWalletId(Long id);
}
