package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Transaction;
import java.util.List;

public interface ITransactionService {

     void add(Transaction t);

     List<Transaction> getAll(int userId);

}
