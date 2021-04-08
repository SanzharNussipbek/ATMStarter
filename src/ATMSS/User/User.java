package ATMSS.User;

/**
 * User class to save the user information during a session
 */
public class User {
    /**
     * Values to be saved
     */
    private String cardNum = null;
    private String pin = null;
    private String[] accounts = null;
    private String currentAcc = null;
    private String credential = null;


    /**
     * Constructor of the class
     */
    public User() { }


    /**
     * Getters of the user instance credential
     * @return Returns user's credential
     */
    public String getCredential() {
        return credential;
    }


    /**
     * Getters of the user instance Card Number
     * @return Returns user's Card Number
     */
    public String getCardNum() {return this.cardNum;}


    /**
     * Getters of the user instance PIN
     * @return Returns user's PIN
     */
    public String getPin() {return this.pin;}


    /**
     * Getters of the user instance accounts
     * @return Returns user's accounts
     */
    public String[] getAccounts() { return this.accounts; }


    /**
     * Getters of the user instance current chosen account
     * @return Returns user's current chosen account
     */
    public String getCurrentAcc() { return this.currentAcc; }


    /**
     * Setter for the current chosen account
     * @param acc Account number
     */
    public void setCurrentAcc(String acc) { this.currentAcc = acc; }


    /**
     * Setter for the user accounts
     * @param accounts Account numbers
     */
    public void setAccounts(String[] accounts) { this.accounts = accounts; }


    /**
     * Setter for the user pin
     * @param pin PIN value
     */
    public void setPin(String pin) {this.pin = pin;}


    /**
     * Setter for the user card number
     * @param cardNum Card Number value
     */
    public void setCardNum(String cardNum) {this.cardNum = cardNum;}


    /**
     * Setter for the User credential
     * @param credential Credential value
     */
    public void setCredential(String credential) {
        this.credential = credential;
    }


    /**
     * Check if the given account exists in user's accounts
     * @param accountNo Given account number
     * @return Return true if exists
     */
    public boolean hasAccount(String accountNo) {
        for (String acc : this.getAccounts()) {
            if (acc.equals(accountNo)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Turn the user instance to string
     * @return Text
     */
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


    /**
     * Reset the user values
     * Turn everything to NULL
     */
    public void reset() {
        this.setPin(null);
        this.setCardNum(null);
        this.setAccounts(null);
        this.setCurrentAcc(null);
        this.setCredential(null);
    }
}
