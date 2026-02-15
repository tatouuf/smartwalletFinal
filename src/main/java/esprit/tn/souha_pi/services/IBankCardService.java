package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.BankCard;
import java.util.List;

public interface IBankCardService {

     void add(BankCard card);

     List<BankCard> getAllByUser(int userId);

     void delete(int id);
}
