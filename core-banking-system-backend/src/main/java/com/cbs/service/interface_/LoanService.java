package com.cbs.service.interface_;

import com.cbs.model.dto.request.LoanApplicationRequest;
import com.cbs.model.dto.response.LoanResponse;
import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    LoanResponse applyForLoan(LoanApplicationRequest request);

    Optional<LoanResponse> getLoanById(Long loanId);

    List<LoanResponse> getLoansByAccountId(Long accountId);

    List<LoanResponse> getLoansByUserId(Long userId);

    List<LoanResponse> getLoansByStatus(LoanStatus status);

    List<LoanResponse> getLoansByLoanType(LoanType loanType);
}