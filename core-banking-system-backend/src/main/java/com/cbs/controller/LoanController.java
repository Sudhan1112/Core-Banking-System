package com.cbs.controller;

import com.cbs.model.dto.request.LoanApplicationRequest;
import com.cbs.model.dto.response.LoanResponse;
import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import com.cbs.service.interface_.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        LoanResponse response = loanService.applyForLoan(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long loanId) {
        return loanService.getLoanById(loanId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<LoanResponse>> getLoansByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(loanService.getLoansByAccountId(accountId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanResponse>> getLoansByStatus(@PathVariable LoanStatus status) {
        return ResponseEntity.ok(loanService.getLoansByStatus(status));
    }

    @GetMapping("/type/{loanType}")
    public ResponseEntity<List<LoanResponse>> getLoansByType(@PathVariable LoanType loanType) {
        return ResponseEntity.ok(loanService.getLoansByLoanType(loanType));
    }
}
