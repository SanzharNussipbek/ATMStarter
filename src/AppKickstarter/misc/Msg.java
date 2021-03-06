package AppKickstarter.misc;


//======================================================================
// Msg
public class Msg {
    private String sender;
    private MBox senderMBox;
    private Type type;
    private String details;

    //------------------------------------------------------------
    // Msg
    /**
     * Constructor for a msg.
     * @param sender id of the msg sender (String)
     * @param senderMBox mbox of the msg sender
     * @param type message type
     * @param details details of the msg (free format String)
     */
    public Msg(String sender, MBox senderMBox, Type type, String details) {
	this.sender = sender;
	this.senderMBox = senderMBox;
	this.type = type;
	this.details = details;
    } // Msg


    //------------------------------------------------------------
    // getSender
    /**
     * Returns the id of the msg sender
     * @return the id of the msg sender
     */
    public String getSender()     { return sender; }


    //------------------------------------------------------------
    // getSenderMBox
    /**
     * Returns the mbox of the msg sender
     * @return the mbox of the msg sender
     */
    public MBox   getSenderMBox() { return senderMBox; }


    //------------------------------------------------------------
    // getType
    /**
     * Returns the message type
     * @return the message type
     */
    public Type   getType()       { return type; }


    //------------------------------------------------------------
    // getDetails
    /**
     * Returns the details of the msg
     * @return the details of the msg
     */
    public String getDetails()    { return details; }


    //------------------------------------------------------------
    // toString
    /**
     * Returns the msg as a formatted String
     * @return the msg as a formatted String
     */
    public String toString() {
	return sender + " (" + type + ") -- " + details;
    } // toString


    //------------------------------------------------------------
    // Msg Types
    /**
     * Message Types used in Msg.
     * @see Msg
     */
    public enum Type {
        /** Terminate the running thread */	Terminate,
        /** Generic error msg */		    Error,
        /** Set a timer */			        SetTimer,
        /** Set a timer */			        CancelTimer,
        /** Timer clock ticks */		    Tick,
        /** Time's up for the timer */		TimesUp,
        /** Health poll */			        Poll,
        /** Health poll acknowledgement */	PollAck,
        /** Cancel */                       Cancel,
        /** Reset */                        Reset,
        /** Shutdown */                     Shutdown,
        /** Update Display */			    TD_UpdateDisplay,
        /** Mouse Clicked */			    TD_MouseClicked,
        /** Append PIN Text*/			    TD_AppendPinText,
        /** Append Amount Text*/            TD_AppendAmountText,
        /** Clear PIN Text*/			    TD_ClearPinText,
        /** Account List*/                  TD_AccountList,
        /** Get Amount */                   TD_GetAmount,
        /** Get Account */                  TD_GetAccount,
        /** Send Account */                 TD_SendAccount,
        /** Account Input */                TD_AccountInput,
        /** Send Balance */                 TD_SendBalance,
        /** Another Service Choice */       TD_AnotherService,
        /** Card inserted */			    CR_CardInserted,
        /** Card removed */			        CR_CardRemoved,
        /** Eject card */			        CR_EjectCard,
        /** Key pressed */			        KP_KeyPressed,
        /** Printer Print */		        PR_Print,
        /** Printer Receipt */              PR_Receipt,
        /** Buzzer play */                  BZ_Play,
        /** Collector Cash Received */      CL_CashReceived,
        /** Dispense Cash */                DP_DispenseCash,
        /** Cash Taken Out */               DP_TakeOutCash,
        /** Main Menu Item */               MainMenuItem,
        /** Account */                      AccountItem,
        /** Amount List*/                   AmountList,
        /** Amount Input */                 AmountInput,
        /** BAMS Login */                   BAMS_Login,
        /** BAMS Enquiry */                 BAMS_Enquiry,
        /** BAMS Accounts */                BAMS_Accounts,
        /** BAMS Withdraw */                BAMS_Withdraw,
        /** BAMS Deposit */                 BAMS_Deposit,
        /** BAMS Transfer */                BAMS_Transfer,
        /** BAMS Change Pin */              BAMS_ChangePin,
        /** BAMS Logout */                  BAMS_Logout,
        /** Admin Menu Item */              ADMIN_MenuItem,
        /** Admin Password */               ADMIN_pwd,
    } // Type
} // Msg
