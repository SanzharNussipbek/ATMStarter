package ATMSS.Account;

public class Account {
    private String cardNum = null;
    private String pin = null;

    public Account(String cardNum, String pin) {
        this.cardNum = cardNum;
        this.pin = pin;
    }

    public Account(String cardNum) {
        this.cardNum = cardNum;
    }

    public Account() {}

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
        return "Card Number: " + this.getCardNum() + ", Password: [" + this.getPin() + "]";
    }

    public void reset() {
        this.setPin(null);
        this.setCardNum(null);
    }

    public void setPin(String pin) {this.pin = pin;}

    public void setCardNum(String cardNum) {this.cardNum = cardNum;}

    public String getCardNum() {return this.cardNum;}

    public String getPin() {return this.pin;}
}
