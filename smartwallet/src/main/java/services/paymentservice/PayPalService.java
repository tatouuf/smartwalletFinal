package services.paymentservice;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PayPalService {

    private PayPalHttpClient client;

    public PayPalService() {
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                "AR6U58yCrmyaBjnxZmLbQZpmrxPgtiYGDAkhRHXaSiMFq6PvdPuPwusn7IS-46aYwB9D_cS5DlZFx8VH",
                "EKkbS5nqEEK5HFAiTci3Ed4A5IHYxORGIsntS8faacOR8Yb_pHKyE7p3K1GqbbIQF7U3v4qvnIWQuu3K"
        );
        this.client = new PayPalHttpClient(environment);
    }

    public String createOrder(double amount, String currency, String description) throws IOException {
        try {
            // Formatage correct du montant (avec point, pas de virgule)
            String formattedAmount = String.format(Locale.US, "%.2f", amount);

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");

            ApplicationContext applicationContext = new ApplicationContext()
                    .brandName("SmartWallet")
                    .landingPage("NO_PREFERENCE")
                    .cancelUrl("https://example.com/cancel")
                    .returnUrl("https://example.com/success")
                    .userAction("PAY_NOW");
            orderRequest.applicationContext(applicationContext);

            AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown()
                    .currencyCode(currency)
                    .value(formattedAmount);

            List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
            purchaseUnits.add(new PurchaseUnitRequest()
                    .amountWithBreakdown(amountWithBreakdown)
                    .description(description));

            orderRequest.purchaseUnits(purchaseUnits);

            OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
            request.prefer("return=representation");

            HttpResponse<Order> response = client.execute(request);
            Order order = response.result();

            return order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .map(link -> link.href())
                    .orElseThrow(() -> new RuntimeException("Lien d'approbation non trouvé"));

        } catch (HttpException e) {
            throw new IOException("Erreur PayPal: " + e.getMessage(), e);
        }
    }
    /**
     * Capture le paiement après approbation
     * @param orderId L'ID de l'ordre PayPal (récupéré de l'URL de retour)
     * @return true si le paiement est capturé avec succès
     */
    public boolean captureOrder(String orderId) {
        try {
            OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
            request.requestBody(new OrderRequest());

            HttpResponse<Order> response = client.execute(request);
            Order order = response.result();

            return "COMPLETED".equals(order.status());

        } catch (IOException e) {
            System.err.println("Erreur lors de la capture: " + e.getMessage());
            return false;
        }
    }
}