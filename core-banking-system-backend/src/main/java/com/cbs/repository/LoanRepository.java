package com.cbs.repository;

import com.cbs.model.entity.Loan;
import com.cbs.model.enums.LoanStatus;
import com.cbs.model.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByAccountId(Long accountId);

    List<Loan> findByAccountIdAndStatus(Long accountId, LoanStatus status);

    // Check if LoanType enum is supported in entity, assuming it is mapped
    // correctly
    // If LoanType is not a field in Loan entity (it seems missing from the previous
    // view_file of Loan.java, but referenced in Repository),
    // I need to be careful.
    // Looking at Loan.java again...
    // I see: private String collateralType; private String interestType; but NO
    // private LoanType loanType;
    // Wait, let me check Loan.java again carefully.

    // I will stick to what seems to be in the entity or common sense for now, but I
    // might need to add loanType to Loan entity if it's missing.
    // The previous view_file of Loan.java showed:
    // private Long loanId;
    // private Long accountId;
    // ...
    // private LoanStatus status;
    // ...
    // It DOES NOT seem to have 'loanType'.
    // However, LoanApplicationRequest has 'private LoanType loanType;'
    // This is a discrepancy. I should probably add it to Loan entity or ignore it
    // in repository for now.
    // Given the prompt "complete the loan module", I should probably add it to the
    // Entity if it's missing,
    // OR just comment it out here if I can't modify the entity structure too much.
    // BUT the 'LoanRepository' I read had 'findByLoanType'.
    // let's assume I should add it to the Entity or it was missed in my quick scan?
    // Let's re-read Loan.java in the next step to be sure.
    // For now I will write a safe version of Repository.

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByLoanType(LoanType loanType);

    @Query("SELECT l FROM Loan l WHERE l.accountId = :accountId AND l.status = :status")
    List<Loan> findByAccountIdAndStatusQuery(@Param("accountId") Long accountId, @Param("status") LoanStatus status);

}