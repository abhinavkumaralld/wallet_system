package com.abhi.wallet.controller;

import com.abhi.wallet.common.ApiResponse;
import com.abhi.wallet.dto.request.CreateWalletRequest;
import com.abhi.wallet.dto.request.DepositRequest;
import com.abhi.wallet.dto.request.TransferRequest;
import com.abhi.wallet.dto.request.WithdrawRequest;
import com.abhi.wallet.dto.response.TransactionResponse;
import com.abhi.wallet.dto.response.WalletResponse;
import com.abhi.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Slf4j
@RequestMapping("/api/wallet")
@RestController
//@CrossOrigin("*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WalletResponse>> create(){
        log.info("user id {}",SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        walletService.create();
        return ResponseEntity.created(URI.create("/done")).body(ApiResponse.created("Wallet created"));
    }

    @DeleteMapping()
    public ResponseEntity<ApiResponse<WalletResponse>> deleteWallet(){
        return ResponseEntity.ok().body(ApiResponse.success(walletService.delete(),"deleted successfully"));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(){
        return ResponseEntity.ok().body(ApiResponse.success(walletService.getWalletByUserId(),"fetched successfully"));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<WalletResponse>> addMoney(@RequestBody DepositRequest depositRequest ){
        log.info("user id {}",depositRequest);
        return ResponseEntity.ok().body(ApiResponse.success(walletService.deposit(depositRequest),"deposited successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<WalletResponse>> withdraw(@RequestBody WithdrawRequest withdrawRequest ){
        log.info("user id {}",withdrawRequest);
        return ResponseEntity.ok().body(ApiResponse.success(walletService.withdraw(withdrawRequest),"withdrawn successfully"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<WalletResponse>> transfer(@RequestBody TransferRequest transferRequest ){
        log.info("user id {}",transferRequest);
        return ResponseEntity.ok().body(ApiResponse.success(walletService.transfer(transferRequest),"transferred successfully"));}

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(){
        return ResponseEntity.ok().body(ApiResponse.success(walletService.getTransactions(),"transactions fetched successfully"));
    }

}
//POST/api/wallet/createCreate wallet for a
//userGET/api/wallet/{userId}Get wallet
//balancePOST/api/wallet/depositAdd money to
//walletPOST/api/wallet/withdrawWithdraw
//moneyPOST/api/wallet/transferTransfer to another
//userGET/api/wallet/{userId}/transactions