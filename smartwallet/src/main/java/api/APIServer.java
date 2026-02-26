package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import entities.User;
import entities.Reclamation;
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
