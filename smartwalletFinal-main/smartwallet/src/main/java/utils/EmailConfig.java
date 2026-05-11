package utils;

public class EmailConfig {

    // EmailJS Configuration
    public static final String EMAILJS_SERVICE_ID = "service_gig4dib";
    public static final String EMAILJS_TEMPLATE_ID = "template_38a8n9b";
    public static final String EMAILJS_PUBLIC_KEY = "BT_2eBX5pGuDEKkhd";

    // ⭐ ADD THIS - Private Key for Desktop/Server Applications
    public static final String EMAILJS_PRIVATE_KEY = "tTCxLbaavfkFkEPDs71Ke"; //

    // API endpoint for private key usage
    public static final String EMAILJS_API_URL = "https://api.emailjs.com/api/v1.0/email/send";

    public static final int CODE_EXPIRATION_MINUTES = 10;
    public static final int CODE_LENGTH = 6;

    private EmailConfig() {}
}