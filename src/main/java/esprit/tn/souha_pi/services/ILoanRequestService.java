package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.LoanRequest;
import java.util.List;

public interface ILoanRequestService {

    void createRequest(int borrowerId, int lenderId, double amount, String message) throws Exception;

    List<LoanRequest> getRequestsForLender(int lenderId);

    void acceptRequest(int requestId) throws Exception;

    void rejectRequest(int requestId) throws Exception;
}
