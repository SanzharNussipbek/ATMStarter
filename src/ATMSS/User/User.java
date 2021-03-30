package ATMSS.User;

import ATMSS.Account.Account;

public class User {
    private String cardNum = null;
    private String pin = null;
    private Account[] accounts = null;
    private Account currentAcc = null;

    public User(String cardNum, String pin, Account[] accounts) {
        this.cardNum = cardNum;
        this.pin = pin;
        this.accounts = accounts;
    }

    public User(String cardNum, String pin) {
        this.cardNum = cardNum;
        this.pin = pin;
    }

    public User(String cardNum) {
        this.cardNum = cardNum;
    }

    public User() {}

    public boolean isValid() {
        return this.hasValidCard() && this.hasValidPin();
    }

    public boolean hasValidCard() {
        return this.getCardNum().length() == 19;
    }

    public boolean hasValidPin() {
        return this.getPin().length() == 6;
    }

    public String toString() {
        String text = "Card Number: " + this.getCardNum()
                + ", Password: [" + this.getPin() + "]";
        if (this.accounts != null) {
            text += ", Accounts: ";
            for (int i = 0; i < this.accounts.length; i++) {
                text += this.accounts[i].toString();
                text += i == this.accounts.length - 1 ? "." : ", ";
            }
        }
        return text;
    }

    public void reset() {
        this.setPin(null);
        this.setCardNum(null);
        this.setAccounts(null);
    }

    public void setCurrentAcc(Account acc) { this.currentAcc = acc; }

    public void setAccounts(Account[] accounts) { this.accounts = accounts; }

    public void setPin(String pin) {this.pin = pin;}

    public void setCardNum(String cardNum) {this.cardNum = cardNum;}

    public String getCardNum() {return this.cardNum;}

    public String getPin() {return this.pin;}

    public Account[] getAccounts() { return this.accounts; }

    public Account getCurrentAcc() { return this.currentAcc; }

    public String getAccountsString() {
        String result = "";
        for (int i = 0; i < this.accounts.length; i++) {
            result += this.accounts[i].getAccountNo();
            result += i == this.accounts.length - 1 ? "" : "/";
        }
        return result;
    }

    public boolean hasAccount(String accountNo) {
        for (Account acc : this.getAccounts()) {
            if (acc.getAccountNo().equals(accountNo)) {
                return true;
            }
        }
        return false;
    }
}
