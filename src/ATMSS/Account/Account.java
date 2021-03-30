package ATMSS.Account;

public class Account {
    private String accountNo;
    private Double amount;

    public Account(String accountNo, Double amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public Account(String accountNo) {
        this.accountNo = accountNo;
    }

    public Account() {}

    public void setAmount(Double amount) {
        setAmount(amount);
    }

    public String getAccountNo() { return this.accountNo; }

    public Double getAmount() { return this.getAmount(); }

    public String toString() {
        return "Account No.: " + this.accountNo + ", Amount: HKD$" + this.amount;
    }
}
