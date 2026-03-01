package com.example.smartwallet.utils;

public class StripeConfig {
    /**
     * ATTENTION : En production, cette clé doit être chargée depuis 
     * un fichier de configuration externe ou une variable d'environnement.
     */
    public static final String SECRET_KEY = System.getenv("STRIPE_SECRET_KEY") != null ? System.getenv("STRIPE_SECRET_KEY") : "";
    public static final String PUBLISHABLE_KEY = System.getenv("STRIPE_PUBLISHABLE_KEY") != null ? System.getenv("STRIPE_PUBLISHABLE_KEY") : "";
}
