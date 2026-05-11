package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Loan;
import java.util.List;

public interface ILoanService {

    Loan getById(int id) throws Exception;

    List<Loan> getLoansForUser(int userId);

    void payLoan(int loanId, int borrowerId, double amount) throws Exception;
}
