package com.cbs.service;

import com.cbs.model.dto.request.DepositRequest;
import com.cbs.model.dto.request.TransferRequest;
import com.cbs.model.dto.request.WithdrawalRequest;
import com.cbs.model.dto.response.TransactionResponse;
import com.cbs.model.entity.Account;
import com.cbs.model.entity.Transaction;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.TransactionStatus;
import com.cbs.model.enums.TransactionType;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.TransactionRepository;
import com.cbs.service.impl.TransactionServiceImpl;
import com.cbs.util.TransactionIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionIdGenerator transactionIdGenerator;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account testAccount;
    private Transaction testTransaction;
    private DepositRequest testDepositRequest;
    private WithdrawalRequest testWithdrawalRequest;
    private TransferRequest testTransferRequest;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setAccountNumber("SB250001001");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setUserId(1L);
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setMinimumBalance(new BigDecimal("1000.00"));

        testTransaction = new Transaction();
        testTransaction.setTransactionId(1L);
        testTransaction.setTransactionReference("TXN2025012612345678901234");
        testTransaction.setTransactionType(TransactionType.DEPOSIT);
        testTransaction.setAmount(new BigDecimal("1000.00"));
        testTransaction.setDestinationAccountId(1L);
        testTransaction.setUserId(1L);
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setBalanceAfter(new BigDecimal("2000.00"));

        testDepositRequest = new DepositRequest();
        testDepositRequest.setAccountId(1L);
        testDepositRequest.setAmount(new BigDecimal("1000.00"));
        testDepositRequest.setDescription("Test deposit");

        testWithdrawalRequest = new WithdrawalRequest();
        testWithdrawalRequest.setAccountId(1L);
        testWithdrawalRequest.setAmount(new BigDecimal("500.00"));
        testWithdrawalRequest.setDescription("Test withdrawal");

        testTransferRequest = new TransferRequest();
        testTransferRequest.setSourceAccountId(1L);
        testTransferRequest.setDestinationAccountId(2L);
        testTransferRequest.setAmount(new BigDecimal("300.00"));
        testTransferRequest.setDescription("Test transfer");
    }

    @Test
    @DisplayName("Should process deposit successfully")
    void deposit_Success() {
        // Arrange
        Account destinationAccount = new Account();
        destinationAccount.setAccountId(1L);
        destinationAccount.setBalance(new BigDecimal("1000.00"));
        destinationAccount.setUserId(1L);
        destinationAccount.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(destinationAccount));
        when(transactionIdGenerator.generateTransactionId()).thenReturn("TXN123456");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(destinationAccount);

        // Act
        TransactionResponse result = transactionService.deposit(testDepositRequest);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        assertEquals(new BigDecimal("1000.00"), result.getAmount());
        assertEquals(1L, result.getDestinationAccountId());
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when depositing to inactive account")
    void deposit_InactiveAccount() {
        // Arrange
        Account inactiveAccount = new Account();
        inactiveAccount.setAccountId(1L);
        inactiveAccount.setStatus(AccountStatus.CLOSED);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(inactiveAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.deposit(testDepositRequest));
        assertEquals("Cannot deposit to inactive account", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should process withdrawal successfully")
    void withdraw_Success() throws Exception {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setAccountId(1L);
        sourceAccount.setBalance(new BigDecimal("2000.00"));
        sourceAccount.setUserId(1L);
        sourceAccount.setStatus(AccountStatus.ACTIVE);
        sourceAccount.setMinimumBalance(new BigDecimal("1000.00"));

        // Create WITHDRAWAL transaction (not DEPOSIT!)
        Transaction withdrawalTransaction = new Transaction();
        withdrawalTransaction.setTransactionId(1L);
        withdrawalTransaction.setTransactionReference("TXN123456");
        withdrawalTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        withdrawalTransaction.setAmount(new BigDecimal("500.00"));
        withdrawalTransaction.setSourceAccountId(1L);
        withdrawalTransaction.setUserId(1L);
        withdrawalTransaction.setStatus(TransactionStatus.COMPLETED);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(transactionIdGenerator.generateTransactionId()).thenReturn("TXN123456");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(withdrawalTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);

        // Act
        TransactionResponse result = transactionService.withdraw(testWithdrawalRequest);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getTransactionType());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(1L, result.getSourceAccountId());
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from inactive account")
    void withdraw_InactiveAccount() {
        // Arrange
        Account inactiveAccount = new Account();
        inactiveAccount.setAccountId(1L);
        inactiveAccount.setBalance(new BigDecimal("2000.00"));  // Add balance
        inactiveAccount.setStatus(AccountStatus.CLOSED);
        inactiveAccount.setMinimumBalance(new BigDecimal("1000.00"));  // Add minimum balance

        when(accountRepository.findById(1L)).thenReturn(Optional.of(inactiveAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.withdraw(testWithdrawalRequest));
        assertEquals("Cannot withdraw from inactive account", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance")
    void withdraw_InsufficientBalance() {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setAccountId(1L);
        sourceAccount.setBalance(new BigDecimal("300.00"));
        sourceAccount.setStatus(AccountStatus.ACTIVE);  // Make it active so balance check happens first
        sourceAccount.setMinimumBalance(new BigDecimal("1000.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> transactionService.withdraw(testWithdrawalRequest));
        assertEquals("Insufficient balance", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should process transfer successfully")
    void transfer_Success() throws Exception {
        // Arrange
        Account sourceAccount = new Account();
        sourceAccount.setAccountId(1L);
        sourceAccount.setBalance(new BigDecimal("2000.00"));
        sourceAccount.setUserId(1L);
        sourceAccount.setStatus(AccountStatus.ACTIVE);
        sourceAccount.setMinimumBalance(new BigDecimal("1000.00"));

        Account destinationAccount = new Account();
        destinationAccount.setAccountId(2L);
        destinationAccount.setBalance(new BigDecimal("1000.00"));
        destinationAccount.setUserId(2L);
        destinationAccount.setStatus(AccountStatus.ACTIVE);

        // Create TRANSFER transaction (not DEPOSIT!)
        Transaction transferTransaction = new Transaction();
        transferTransaction.setTransactionId(1L);
        transferTransaction.setTransactionReference("TXN123456");
        transferTransaction.setTransactionType(TransactionType.TRANSFER);
        transferTransaction.setAmount(new BigDecimal("300.00"));
        transferTransaction.setSourceAccountId(1L);
        transferTransaction.setDestinationAccountId(2L);
        transferTransaction.setUserId(1L);
        transferTransaction.setStatus(TransactionStatus.COMPLETED);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destinationAccount));
        when(transactionIdGenerator.generateTransactionId()).thenReturn("TXN123456", "TXN123457");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transferTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount, destinationAccount);

        // Act
        TransactionResponse result = transactionService.transfer(testTransferRequest);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.TRANSFER, result.getTransactionType());
        assertEquals(new BigDecimal("300.00"), result.getAmount());
        assertEquals(1L, result.getSourceAccountId());
        assertEquals(2L, result.getDestinationAccountId());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when transferring from inactive account")
    void transfer_InactiveSourceAccount() {
        // Arrange
        Account inactiveAccount = new Account();
        inactiveAccount.setAccountId(1L);
        inactiveAccount.setStatus(AccountStatus.CLOSED);

        Account activeAccount = new Account();
        activeAccount.setAccountId(2L);
        activeAccount.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(inactiveAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(activeAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.transfer(testTransferRequest));
        assertEquals("Cannot transfer from inactive source account", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should get transaction by ID successfully")
    void getTransactionById_Success() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act
        Optional<TransactionResponse> result = transactionService.getTransactionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TXN2025012612345678901234", result.get().getTransactionReference());
        verify(transactionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get transactions by account ID successfully")
    void getTransactionsByAccountId_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByAccountId(1L)).thenReturn(transactions);

        // Act
        List<TransactionResponse> result = transactionService.getTransactionsByAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("TXN2025012612345678901234", result.get(0).getTransactionReference());
        verify(transactionRepository).findByAccountId(1L);
    }

    @Test
    @DisplayName("Should reverse transaction successfully")
    void reverseTransaction_Success() throws Exception {
        // Arrange
        Transaction reversalTransaction = new Transaction();
        reversalTransaction.setTransactionId(2L);
        reversalTransaction.setTransactionType(TransactionType.REFUND);
        reversalTransaction.setAmount(new BigDecimal("1000.00"));
        reversalTransaction.setStatus(TransactionStatus.COMPLETED);

        Account account = new Account();
        account.setAccountId(1L);
        account.setBalance(new BigDecimal("2000.00"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionIdGenerator.generateTransactionId()).thenReturn("TXN123456");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(reversalTransaction);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        TransactionResponse result = transactionService.reverseTransaction(1L);

        // Assert
        assertNotNull(result);
        assertEquals(TransactionType.REFUND, result.getTransactionType());
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(transactionRepository, times(2)).save(any(Transaction.class));  // FIXED: Changed from 1 to 2
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when reversing non-completed transaction")
    void reverseTransaction_NotCompleted() {
        // Arrange
        testTransaction.setStatus(TransactionStatus.PENDING);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> transactionService.reverseTransaction(1L));
        assertEquals("Cannot reverse a non-completed transaction", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should calculate total debits successfully")
    void getTotalDebits_Success() {
        // Arrange
        when(transactionRepository.sumDebitsByAccountIdAndStatus(1L, TransactionStatus.COMPLETED, TransactionType.WITHDRAWAL))
                .thenReturn(new BigDecimal("1500.00"));

        // Act
        BigDecimal result = transactionService.getTotalDebits(1L);

        // Assert
        assertEquals(new BigDecimal("1500.00"), result);
        verify(transactionRepository).sumDebitsByAccountIdAndStatus(1L, TransactionStatus.COMPLETED, TransactionType.WITHDRAWAL);
    }

    @Test
    @DisplayName("Should calculate total credits successfully")
    void getTotalCredits_Success() {
        // Arrange
        when(transactionRepository.sumCreditsByAccountIdAndStatus(1L, TransactionStatus.COMPLETED, TransactionType.DEPOSIT))
                .thenReturn(new BigDecimal("2000.00"));

        // Act
        BigDecimal result = transactionService.getTotalCredits(1L);

        // Assert
        assertEquals(new BigDecimal("2000.00"), result);
        verify(transactionRepository).sumCreditsByAccountIdAndStatus(1L, TransactionStatus.COMPLETED, TransactionType.DEPOSIT);
    }
}