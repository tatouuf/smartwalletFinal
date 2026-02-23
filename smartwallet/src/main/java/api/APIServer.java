package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import entities.User;
import entities.Reclamation;
import services.ServicePasswordReset;
import services.ServiceUser;
import services.ServiceReclamation;
import services.AIService;
import utils.PasswordUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class APIServer {

    private static final Logger logger = Logger.getLogger(APIServer.class.getName());
    private static final int PORT = 8081;
    private static final Gson gson = new Gson();
    private static HttpServer server;

    private static final ServiceUser userService = new ServiceUser();
    private static final ServiceReclamation reclamationService = new ServiceReclamation();
    private static final AIService aiService = AIService.getInstance();

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // API Endpoints
            server.createContext("/api/users", new UsersHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/reclamations", new ReclamationsHandler());
            server.createContext("/api/ai/categorize", new AICategorizeHandler());
            server.createContext("/api/ai/sentiment", new AISentimentHandler());
            server.createContext("/api/health", new HealthHandler());
            server.createContext("/reset", new PasswordResetWebHandler());
            server.setExecutor(null);
            server.start();

            logger.info("API Server started on port " + PORT);
            System.out.println("✅ API Server running at http://localhost:" + PORT);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start API server", e);
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            logger.info("API Server stopped");
        }
    }

    // ==================== HEALTH CHECK ====================
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "OK");
                response.put("message", "SmartWallet API is running");
                response.put("version", "1.0.0");

                sendResponse(exchange, 200, gson.toJson(response));
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    // ==================== USERS ENDPOINT ====================
    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            try {
                if ("GET".equals(method)) {
                    // GET /api/users - List all users
                    List<User> users = userService.recupererUsersOnly();
                    List<UserDTO> userDTOs = users.stream()
                            .map(u -> new UserDTO(u.getId(), u.getNom(), u.getPrenom(), u.getEmail()))
                            .collect(Collectors.toList());

                    sendResponse(exchange, 200, gson.toJson(userDTOs));

                } else if ("POST".equals(method)) {
                    // POST /api/users - Create user
                    String body = readRequestBody(exchange);
                    UserCreateRequest request = gson.fromJson(body, UserCreateRequest.class);

                    if (userService.isEmailTaken(request.email)) {
                        sendResponse(exchange, 400, "{\"error\":\"Email already exists\"}");
                        return;
                    }

                    User newUser = new User();
                    newUser.setNom(request.nom);
                    newUser.setPrenom(request.prenom);
                    newUser.setEmail(request.email);
                    newUser.setPassword(PasswordUtils.hashPassword(request.password));
                    newUser.setTelephone(request.telephone);
                    newUser.setRole(entities.Role.USER);
                    newUser.setDate_creation(java.time.LocalDateTime.now());
                    newUser.setDate_update(java.time.LocalDateTime.now());
                    newUser.setIs_actif(true);

                    userService.ajouter(newUser);

                    sendResponse(exchange, 201, "{\"message\":\"User created successfully\"}");

                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Database error", e);
                sendResponse(exchange, 500, "{\"error\":\"Database error\"}");
            }
        }
    }

    // ==================== LOGIN ENDPOINT ====================
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readRequestBody(exchange);
                LoginRequest request = gson.fromJson(body, LoginRequest.class);

                User user = userService.login(request.email, request.password);

                if (user != null) {
                    LoginResponse response = new LoginResponse(
                            user.getId(),
                            user.getNom(),
                            user.getPrenom(),
                            user.getEmail(),
                            user.getRole().name()
                    );
                    sendResponse(exchange, 200, gson.toJson(response));
                } else {
                    sendResponse(exchange, 401, "{\"error\":\"Invalid credentials\"}");
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Database error", e);
                sendResponse(exchange, 500, "{\"error\":\"Database error\"}");
            }
        }
    }

    // ==================== RECLAMATIONS ENDPOINT ====================
    static class ReclamationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            try {
                if ("GET".equals(method)) {
                    // GET /api/reclamations - List all reclamations
                    List<Reclamation> reclamations = reclamationService.getAllReclamations();
                    sendResponse(exchange, 200, gson.toJson(reclamations));

                } else if ("POST".equals(method)) {
                    // POST /api/reclamations - Create reclamation
                    String body = readRequestBody(exchange);
                    ReclamationCreateRequest request = gson.fromJson(body, ReclamationCreateRequest.class);

                    reclamationService.sendReclamation(request.userId, request.message);

                    sendResponse(exchange, 201, "{\"message\":\"Reclamation created successfully\"}");

                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Database error", e);
                sendResponse(exchange, 500, "{\"error\":\"Database error\"}");
            }
        }
    }

    // ==================== AI CATEGORIZATION ENDPOINT ====================
    static class AICategorizeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String body = readRequestBody(exchange);
            Map<String, String> request = gson.fromJson(body, Map.class);
            String message = request.get("message");

            if (message == null || message.isEmpty()) {
                sendResponse(exchange, 400, "{\"error\":\"Message is required\"}");
                return;
            }

            String category = aiService.categorizeReclamation(message);

            Map<String, String> response = new HashMap<>();
            response.put("category", category);
            response.put("message", message);

            sendResponse(exchange, 200, gson.toJson(response));
        }
    }

    // ==================== AI SENTIMENT ANALYSIS ENDPOINT ====================
    static class AISentimentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            String body = readRequestBody(exchange);
            Map<String, String> request = gson.fromJson(body, Map.class);
            String text = request.get("text");

            if (text == null || text.isEmpty()) {
                sendResponse(exchange, 400, "{\"error\":\"Text is required\"}");
                return;
            }

            AIService.SentimentResult sentiment = aiService.analyzeSentiment(text);

            Map<String, Object> response = new HashMap<>();
            response.put("score", sentiment.getScore());
            response.put("sentiment", sentiment.getSentiment());
            response.put("isUrgent", sentiment.isUrgent());
            response.put("text", text);

            sendResponse(exchange, 200, gson.toJson(response));
        }
    }

    // ==================== HELPER METHODS ====================

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    // ==================== PASSWORD RESET WEB ENDPOINT ====================
    static class PasswordResetWebHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                // Parse query parameters
                String query = exchange.getRequestURI().getQuery();
                String token = null;

                if (query != null) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                            token = keyValue[1];
                            break;
                        }
                    }
                }

                if (token == null || token.isEmpty()) {
                    String html = createErrorPage("Invalid Reset Link",
                            "The password reset link is invalid or incomplete.");
                    sendHtmlResponse(exchange, 400, html);
                    return;
                }

                // Validate token
                ServicePasswordReset resetService = new ServicePasswordReset();
                var resetToken = resetService.validateToken(token);

                if (resetToken == null) {
                    String html = createErrorPage("Expired Link",
                            "This password reset link has expired or has already been used.<br>" +
                                    "Please request a new password reset from the application.");
                    sendHtmlResponse(exchange, 400, html);
                    return;
                }

                // Token is valid - show success page with instructions
                String html = createSuccessPage(token);
                sendHtmlResponse(exchange, 200, html);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error handling password reset", e);
                String html = createErrorPage("Server Error",
                        "An error occurred. Please try again later.");
                sendHtmlResponse(exchange, 500, html);
            }
        }

        private String createSuccessPage(String token) {
            return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Reset Password - SmartWallet</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }
                    .container { 
                        background: white;
                        border-radius: 15px;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                        padding: 50px;
                        max-width: 500px;
                        width: 100%;
                        text-align: center;
                    }
                    .icon { 
                        font-size: 64px;
                        margin-bottom: 20px;
                        animation: bounce 1s infinite;
                    }
                    @keyframes bounce {
                        0%, 100% { transform: translateY(0); }
                        50% { transform: translateY(-10px); }
                    }
                    h1 { 
                        color: #333;
                        margin-bottom: 20px;
                        font-size: 28px;
                    }
                    .success-message {
                        background: #d4edda;
                        border: 2px solid #28a745;
                        border-radius: 10px;
                        padding: 20px;
                        margin: 20px 0;
                        color: #155724;
                    }
                    .info-box {
                        background: #f8f9fa;
                        border-left: 4px solid #667eea;
                        padding: 15px;
                        margin: 20px 0;
                        text-align: left;
                    }
                    .token {
                        background: #f0f0f0;
                        padding: 10px;
                        border-radius: 5px;
                        font-family: monospace;
                        word-break: break-all;
                        margin: 10px 0;
                        font-size: 12px;
                    }
                    .steps {
                        text-align: left;
                        margin: 20px 0;
                    }
                    .steps li {
                        margin: 10px 0;
                        padding-left: 10px;
                    }
                    .warning {
                        background: #fff3cd;
                        border: 2px solid #ffc107;
                        border-radius: 10px;
                        padding: 15px;
                        margin: 20px 0;
                        color: #856404;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="icon">✅</div>
                    <h1>Password Reset Link Verified</h1>
                    
                    <div class="success-message">
                        <strong>✓ Your reset link is valid!</strong><br>
                        Now return to the SmartWallet application to complete your password reset.
                    </div>

                    <div class="info-box">
                        <strong>📋 Your Reset Token:</strong>
                        <div class="token">%s</div>
                        <small style="color: #666;">Copy this token if needed</small>
                    </div>

                    <div class="steps">
                        <strong>Next Steps:</strong>
                        <ol>
                            <li>Go back to the SmartWallet application</li>
                            <li>The reset password page should open automatically</li>
                            <li>Enter your new password</li>
                            <li>Click "Reset Password"</li>
                        </ol>
                    </div>

                    <div class="warning">
                        <strong>⚠️ Important:</strong><br>
                        This token will expire in 1 hour and can only be used once.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(token);
        }

        private String createErrorPage(String title, String message) {
            return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Error - SmartWallet</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
                        background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }
                    .container { 
                        background: white;
                        border-radius: 15px;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                        padding: 50px;
                        max-width: 500px;
                        width: 100%;
                        text-align: center;
                    }
                    .icon { 
                        font-size: 64px;
                        margin-bottom: 20px;
                    }
                    h1 { 
                        color: #dc3545;
                        margin-bottom: 20px;
                        font-size: 28px;
                    }
                    .message {
                        color: #666;
                        line-height: 1.6;
                        margin-bottom: 30px;
                    }
                    .action {
                        background: #667eea;
                        color: white;
                        padding: 15px 30px;
                        border-radius: 10px;
                        text-decoration: none;
                        display: inline-block;
                        font-weight: bold;
                        transition: all 0.3s;
                    }
                    .action:hover {
                        background: #5568d3;
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="icon">❌</div>
                    <h1>%s</h1>
                    <p class="message">%s</p>
                    <a href="#" class="action" onclick="window.close()">Close Window</a>
                </div>
            </body>
            </html>
            """.formatted(title, message);
        }

        private void sendHtmlResponse(HttpExchange exchange, int statusCode, String html) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(statusCode, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        return new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    // ==================== DTO CLASSES ====================

    static class UserDTO {
        int id;
        String nom;
        String prenom;
        String email;

        UserDTO(int id, String nom, String prenom, String email) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
        }
    }

    static class UserCreateRequest {
        String nom;
        String prenom;
        String email;
        String password;
        String telephone;
    }

    static class LoginRequest {
        String email;
        String password;
    }

    static class LoginResponse {
        int id;
        String nom;
        String prenom;
        String email;
        String role;

        LoginResponse(int id, String nom, String prenom, String email, String role) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.role = role;
        }
    }

    static class ReclamationCreateRequest {
        int userId;
        String message;
    }
}