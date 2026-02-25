package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.LoanPayment;
import java.sql.SQLException;
import java.util.List;

public interface ILoanPaymentService {

    void add(LoanPayment payment) throws SQLException;

    List<LoanPayment> getByLoan(int loanId);
}
