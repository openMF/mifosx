package org.mifosng.platform.client.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosng.platform.infrastructure.AbstractAuditableCustom;
import org.mifosng.platform.loan.domain.Loan;
import org.mifosng.platform.loan.domain.LoanTransaction;
import org.mifosng.platform.saving.domain.DepositAccount;
import org.mifosng.platform.user.domain.AppUser;

@Entity
@Table(name = "m_note")
public class Note extends AbstractAuditableCustom<AppUser, Long> {

	@ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private final Client       client;
    
    @SuppressWarnings("unused")
	@ManyToOne
    @JoinColumn(name = "loan_id", nullable = true)
    private Loan       loan;
    
    @SuppressWarnings("unused")
	@ManyToOne
    @JoinColumn(name = "loan_transaction_id", nullable = true)
    private LoanTransaction       loanTransaction;

    @SuppressWarnings("unused")
	@Column(name = "note", length=1000)
    private String       note;
    
    @SuppressWarnings("unused")
	@Column(name = "note_type_enum")
    private Integer       noteTypeId;
    
    @ManyToOne
    @JoinColumn(name = "deposit_account_id", nullable = true)
    private DepositAccount depositAccount;
    
    public enum NoteType {
        CLIENT(100, "noteType.client"), LOAN(200, "noteType.loan"), LOAN_TRANSACTION(300, "noteType.loan.transaction"), DEPOSIT(400, "noteType.deposit");

        private Integer value;
        private String code;

        NoteType(final Integer value, final String code) { this.value = value; this.code = code; }    

        public Integer getValue() { return value; }

        public String getCode() {
			return code;
		}

		public static NoteType parse(Integer id) {
        	NoteType right = null; // Default
            for (NoteType item : NoteType.values()) {
                if (item.getValue().intValue() == id.intValue()) {
                    right = item;
                    break;
                }
            }
            return right;
        }
    }

    public static Note clientNote(Client client, String note) {
		return new Note(client, note);
	}

	public static Note loanNote(Loan loan,  String note) {
		return new Note(loan, note);
	}
	
	public static Note loanTransactionNote(Loan loan, LoanTransaction loanTransaction, String note) {
		return new Note(loan, loanTransaction, note);
	}
    
    private Note(Client client, String note) {
		this.client = client;
		this.note = note;
		this.noteTypeId = NoteType.CLIENT.getValue();
	}
    
    private Note(Loan loan, String note) {
    	this.loan = loan;
		this.client = loan.client();
		this.note = note;
		this.noteTypeId = NoteType.LOAN.getValue();
	}
    
    private Note(Loan loan, LoanTransaction loanTransaction, String note) {
    	this.loan = loan;
    	this.loanTransaction = loanTransaction;
		this.client = loan.client();
		this.note = note;
		this.noteTypeId = NoteType.LOAN_TRANSACTION.getValue();
	}
    
    protected Note() {
        this.client = null;
        this.loan = null;
        this.loanTransaction = null;
        this.note = null;
        this.noteTypeId = null;
    }

	public Note(DepositAccount account, String note) {
		this.depositAccount=account;
		this.client = account.client();
		this.note = note;
		this.noteTypeId = NoteType.DEPOSIT.getValue();
	}

	public void update(final String note) {
		this.note = note;
	}

	public boolean isNotAgainstClientWithIdOf(Long clientId) {
		return !this.client.identifiedBy(clientId);
	}

	public static Note depositNote(DepositAccount account, String noteText) {
		return new Note(account,noteText);
	}
}