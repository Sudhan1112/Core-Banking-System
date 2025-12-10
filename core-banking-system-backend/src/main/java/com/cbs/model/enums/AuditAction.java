package com.cbs.model.enums;

public enum AuditAction {
    CREATE("Create"),
    READ("Read"),
    UPDATE("Update"),
    DELETE("Delete"),
    LOGIN("Login"),
    LOGOUT("Logout"),
    REGISTER("Register"),
    DEPOSIT("Deposit"),
    WITHDRAW("Withdraw"),
    TRANSFER("Transfer"),
    LOAN_APPLICATION("Loan Application"),
    LOAN_APPROVAL("Loan Approval"),
    LOAN_REJECTION("Loan Rejection"),
    KYC_UPLOAD("KYC Upload"),
    KYC_APPROVAL("KYC Approval"),
    KYC_REJECTION("KYC Rejection"),
    PASSWORD_CHANGE("Password Change"),
    ACCOUNT_LOCK("Account Lock"),
    ACCOUNT_UNLOCK("Account Unlock");
    
    private final String displayName;
    
    AuditAction(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}