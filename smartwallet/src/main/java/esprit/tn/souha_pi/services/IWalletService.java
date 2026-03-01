package esprit.tn.souha_pi.services;

import esprit.tn.souha_pi.entities.Wallet;

public interface IWalletService {

    /* ================= BASIC WALLET ================= */

    Wallet getByUserId(int userId) throws Exception;

    void credit(int userId, double amount) throws Exception;

    void debit(int userId, double amount) throws Exception;

    void transfer(int senderId, int receiverId, double amount) throws Exception;


    Wallet getWallet();

    boolean send(double amount);

    void addBalance(int userId, double amount);

    void addBalance(double amount);

    void updateWallet(Wallet wallet);
}
