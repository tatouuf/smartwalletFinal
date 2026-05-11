package services.sms;

public class SmsItafSimule {

    public static boolean envoyer(String to, String message) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📱 SMS ITAF - SIMULATION");
        System.out.println("📞 Destinataire: " + to);
        System.out.println("💬 Message: " + message);
        System.out.println("=".repeat(50) + "\n");
        return true;
    }

    public static String formaterNumero(String telephone) {
        telephone = telephone.replaceAll("[^0-9+]", "");
        if (telephone.startsWith("0")) {
            telephone = "+216" + telephone.substring(1);
        }
        if (!telephone.startsWith("+")) {
            telephone = "+216" + telephone;
        }
        return telephone;
    }
}