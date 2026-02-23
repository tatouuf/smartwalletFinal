package teste;

import com.example.smartwallet.Services.ServiceRecurringPayment;

public class Main {
    public static void main(String[] args) {

        try {
            ServiceRecurringPayment service = new ServiceRecurringPayment();
            System.out.println(service.recuperer());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
