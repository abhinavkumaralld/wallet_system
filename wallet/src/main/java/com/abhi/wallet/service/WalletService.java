package com.abhi.wallet.service;

import com.abhi.wallet.dto.request.DepositRequest;
import com.abhi.wallet.dto.request.TransferEvent;
import com.abhi.wallet.dto.request.TransferRequest;
import com.abhi.wallet.dto.request.WithdrawRequest;
import com.abhi.wallet.dto.response.TransactionResponse;
import com.abhi.wallet.dto.response.UserDetailsResponse;
import com.abhi.wallet.dto.response.WalletResponse;
import com.abhi.wallet.entity.Transaction;
import com.abhi.wallet.entity.Wallet;
import com.abhi.wallet.enums.TransactionStatus;
import com.abhi.wallet.enums.TransactionType;
import com.abhi.wallet.exception.BadRequestException;
import com.abhi.wallet.exception.DuplicateTransactionException;
import com.abhi.wallet.exception.ResourceNotFound;
import com.abhi.wallet.repository.TransactionRepository;
import com.abhi.wallet.repository.WalletRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.auth.AuthenticationContext;
import org.slf4j.ILoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    public WalletService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public WalletResponse create() throws BadRequestException {
        Long userId = Long.valueOf(
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        log.info("create   {}",userId);


        Wallet wallet = walletRepository.getByUserId(userId).orElse(null);

        if (wallet != null) {
            throw new BadRequestException("Wallet already exist for this user");
        }
        Wallet wallet1 = Wallet.builder()
                .balance(BigDecimal.valueOf(0L))
                .userId(userId)
                .build();
        return mapWalletToWalletResponse(walletRepository.save(wallet1));
    }

//    @Cacheable(cacheNames = "wallet",key = "#userId")
    public WalletResponse getWalletByUserId() throws BadRequestException {
        Long userId = Long.valueOf(
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        log.info("get   {}",userId);
        Wallet wallet = walletRepository.getByUserId(userId).orElse(null);

        if (wallet == null) {
            throw new BadRequestException("Wallet not exist for this user");
        }
        return mapWalletToWalletResponse(wallet);
    }

    @Transactional
//    @CachePut(cacheNames = "wallet",key = "#depositRequest.userId")
    public WalletResponse deposit(DepositRequest depositRequest) {
        if (depositRequest.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new BadRequestException("amount should be more than 0");
        }
        Long userId = Long.valueOf(
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        log.info("deposit {}",userId);
        Wallet wallet = walletRepository.getByUserId(userId).orElse(null);
        if (wallet == null) {
            throw new ResourceNotFound("NO wallet found , Pls create one");
        }
        if (transactionRepository.existsByReferenceId(depositRequest.getReferenceId())) {
            throw new DuplicateTransactionException("Transaction duplicated");
        }
        wallet.setBalance(wallet.getBalance().add(depositRequest.getAmount()));

        Transaction transaction = Transaction.builder()
                .amount(depositRequest.getAmount())
                .walletId(wallet.getId())
                .type(TransactionType.DIPOSIT)
                .status(TransactionStatus.PENDING)
                .description(depositRequest.getDescription())
                .referenceId(depositRequest.getReferenceId())
                .receiverUserId(null)
                .senderUserId(null)
                .build();
        Transaction storedTransaction = transactionRepository.save(transaction);
        Wallet wallet1 = walletRepository.save(wallet);

        storedTransaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(storedTransaction);

        return mapWalletToWalletResponse(wallet1);
    }

    @Transactional
//    @CachePut(cacheNames = "wallet",key = "#withdrawRequest.userId")
    public WalletResponse withdraw(WithdrawRequest withdrawRequest) {
        if (transactionRepository.existsByReferenceId(withdrawRequest.getReferenceId())) {
            throw new DuplicateTransactionException("duplicate transaction");
        }
        Long userId = Long.valueOf(
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        log.info("withdraw {}",userId);
        Wallet wallet = walletRepository.getByUserId(userId).orElseThrow(() -> new ResourceNotFound("Wallet not found, Pls create one"));
        if (wallet.getBalance().compareTo(withdrawRequest.getAmount()) < 0) {
            throw new BadRequestException("Insufficient amount");
        }
        wallet.setBalance(wallet.getBalance().subtract(withdrawRequest.getAmount()));
        Transaction transaction = Transaction.builder()
                .receiverUserId(null)
                .senderUserId(null)
                .walletId(wallet.getId())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .amount(withdrawRequest.getAmount())
                .description(withdrawRequest.getDescription())
                .referenceId(withdrawRequest.getReferenceId())
                .build();


        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        return mapWalletToWalletResponse(wallet);
    }

    @Transactional
//    @CachePut(cacheNames = "wallet",key = "#transferRequest.userId")
    public WalletResponse transfer(TransferRequest transferRequest){
        if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount should be more than 0");
        }
        Long userId = Long.valueOf(                   // same as sender userId
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        if (userId
                .equals(transferRequest.getReceiverUserId())) {
            throw new BadRequestException("Cannot transfer to your own wallet");
        }
        log.info("transfer from {} to {}",userId,transferRequest.getReceiverUserId());

        if(transactionRepository.existsByReferenceId(transferRequest.getReferenceId())){
            throw new DuplicateTransactionException("Duplicate transfer not allowed");
        }
        Wallet senderW=walletRepository.getByUserId(userId).
                orElseThrow(()->new ResourceNotFound("sender wallet not found"));
        Wallet receiverW=walletRepository.getByUserId(transferRequest.getReceiverUserId())
                .orElseThrow(()->new ResourceNotFound("receiver wallet not found"));

        Long firstId =Math.min(senderW.getId(), receiverW.getId());
        Long secondId=Math.max(senderW.getId(), receiverW.getId());

        Wallet firstLock=walletRepository.findByIdWithLock(firstId).orElse(null);
        Wallet secondLock=walletRepository.findByIdWithLock(secondId).orElse(null);


        Wallet senderWallet=senderW.getId()==firstLock.getId()?firstLock:secondLock;
        Wallet receiverWallet=receiverW.getId()==firstLock.getId()?firstLock:secondLock;

        if(senderWallet.getBalance().compareTo(transferRequest.getAmount())<0){
            throw new BadRequestException("Money not enough");
        }
        senderWallet.setBalance(senderWallet.getBalance().subtract(transferRequest.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(transferRequest.getAmount()));

        walletRepository.save(receiverWallet);
        Wallet v=walletRepository.save(senderWallet);

        Transaction transaction = Transaction.builder()
                .receiverUserId(transferRequest.getReceiverUserId())
                .senderUserId(userId)
                .walletId(senderWallet.getId())
                .type(TransactionType.DEBIT)
                .status(TransactionStatus.SUCCESS)
                .amount(transferRequest.getAmount())
                .description(transferRequest.getDescription())
                .referenceId(transferRequest.getReferenceId())
                .build();
        Transaction transaction1 = Transaction.builder()
                .receiverUserId(transferRequest.getReceiverUserId())
                .senderUserId(userId)
                .walletId(receiverWallet.getId())
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SUCCESS)
                .amount(transferRequest.getAmount())
                .description(transferRequest.getDescription())
                .referenceId(transferRequest.getReferenceId())
                .build();

        transactionRepository.save(transaction);
        transactionRepository.save(transaction1);

        List<UserDetailsResponse> users = authServiceClient
                .getUserDetailsBatch(userId,
                        transferRequest.getReceiverUserId());

        log.info("sending from {} to {}",userId,transferRequest.getReceiverUserId());
        UserDetailsResponse sender   = users.get(0);
        UserDetailsResponse receiver = users.get(1);
        TransferEvent transferEvent =TransferEvent.builder()
                .amount(transferRequest.getAmount())
                .transferredAt(LocalDateTime.now())
                .referenceId(transferRequest.getReferenceId())
                .senderUserId(userId)
                .receiverUserId(transferRequest.getReceiverUserId())
                .senderEmail(sender.getEmail())
                .senderName(sender.getName())
                .receiverEmail(receiver.getEmail())
                .receiverName(receiver.getName())
                .build();
        System.out.println("event "+transferEvent.toString());
        applicationEventPublisher.publishEvent(transferEvent);
        return mapWalletToWalletResponse(v);
    }

    public Page<TransactionResponse> getTransactions(int page,int size,String sortBy){
        Long userId = Long.valueOf(                   // same as sender userId
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        log.info("get transactions {}",userId);
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,sortBy));
        Wallet wallet=walletRepository.getByUserId(userId).orElseThrow(()->new ResourceNotFound("wallet not found"));
        Page<Transaction> transactions=transactionRepository.findAllByWalletId(wallet.getId(),pageable);

        return transactions
                .map((t)->mapTransactionToTransactionResponse(t));
    }

    @Transactional
//    @CacheEvict(cacheNames = "wallet",key = "#userId")
    public WalletResponse delete() throws BadRequestException {
        Long userId = Long.valueOf(
                (String) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
        Wallet wallet = walletRepository.getByUserId(userId).orElse(null);

        if (wallet == null) {
            throw new BadRequestException("Wallet does not exist for this user");
        }
        walletRepository.deleteByUserId(userId);
        return mapWalletToWalletResponse(wallet);
    }
    // UTIL Functions

    public WalletResponse mapWalletToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .version(wallet.getVersion())
                .createdAt(wallet.getCreatedAt())
                .build();
    }

    public TransactionResponse mapTransactionToTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .description(transaction.getDescription())
                .walletId(transaction.getWalletId())
                .type(transaction.getType())
                .referenceId(transaction.getReferenceId())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .receiverUserId(transaction.getReceiverUserId())
                .senderUserId(transaction.getSenderUserId())
                .build();
    }
}
