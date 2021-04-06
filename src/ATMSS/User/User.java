package ATMSS.User;

import ATMSS.Account.Account;

public class User {
    private String cardNum = null;
    private String pin = null;
    private String[] accounts = null;
    private String currentAcc = null;
    private String credential = null;

    public User(String cardNum, String pin, String[] accounts) {
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

    public User() {
        this.credential = "OK";
    }

    public String toString() {
        String text = "\nCard Number: " + this.getCardNum()
                + "\nPIN: [" + this.getPin() + "]";
        if (this.accounts != null) {
            text += "\nAccounts: [";
            for (int i = 0; i < this.accounts.length; i++) {
                text += this.accounts[i];
                text += i == this.accounts.length - 1 ? "." : "/";
            }
            text += "]";
        }
        if (this.currentAcc != null) {
            text += "\nCurrent Acc: [" + this.currentAcc + "]";
        }
        return text;
    }

    public void reset() {
        this.setPin(null);
        this.setCardNum(null);
        this.setAccounts(null);
        this.setCurrentAcc(null);
        this.setCredential(null);
    }

    public void setCurrentAcc(String acc) { this.currentAcc = acc; }

    public void setAccounts(String[] accounts) { this.accounts = accounts; }

    public void setPin(String pin) {this.pin = pin;}

    public void setCardNum(String cardNum) {this.cardNum = cardNum;}

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getCredential() {
        return credential;
    }

    public String getCardNum() {return this.cardNum;}

    public String getPin() {return this.pin;}

    public String[] getAccounts() { return this.accounts; }

    public String getCurrentAcc() { return this.currentAcc; }

    public String getAccountsString() {
        String result = "";
        for (int i = 0; i < this.accounts.length; i++) {
            result += this.accounts[i];
            result += i == this.accounts.length - 1 ? "" : "/";
        }
        return result;
    }

    public boolean hasAccount(String accountNo) {
        for (String acc : this.getAccounts()) {
            if (acc.equals(accountNo)) {
                return true;
            }
        }
        return false;
    }
}
