package esprit.tn.chayma.services;

import esprit.tn.chayma.entities.Depense;
import esprit.tn.chayma.entities.Planning;
import java.util.*;
import java.util.stream.Collectors;

public class PlanningService {
    private static PlanningService instance;
    private Map<Integer, List<Planning>> userPlannings;
    private Map<Integer, List<Depense>> planningDepenses;
    private int nextPlanningId;
    private int nextDepenseId;

    private PlanningService() {
        userPlannings = new HashMap<>();
        planningDepenses = new HashMap<>();
        nextPlanningId = 1;
        nextDepenseId = 1;

        // Données de test
        initTestData();
    }

    public static PlanningService getInstance() {
        if (instance == null) {
            instance = new PlanningService();
        }
        return instance;
    }

    private void initTestData() {
        // Planning 1: Courses supermarché
        Planning p1 = new Planning(nextPlanningId++, 1, "Courses Supermarché",
                "Achats alimentaires pour le mois", "Alimentation", 500,
                java.time.LocalDateTime.now().plusDays(30));

        p1.ajouterDepense(new Depense(nextDepenseId++, p1.getId(), "Lait", 15, "Alimentation", "2 briques de lait"));
        p1.ajouterDepense(new Depense(nextDepenseId++, p1.getId(), "Yaourts", 25, "Alimentation", "Pack de 12 yaourts"));
        p1.ajouterDepense(new Depense(nextDepenseId++, p1.getId(), "Fromage", 30, "Alimentation", "Fromage râpé"));
        p1.ajouterDepense(new Depense(nextDepenseId++, p1.getId(), "Fruits", 40, "Alimentation", "Pommes, bananes"));

        userPlannings.computeIfAbsent(1, k -> new ArrayList<>()).add(p1);

        // Planning 2: Entretien voiture
        Planning p2 = new Planning(nextPlanningId++, 1, "Entretien Voiture",
                "Entretien régulier de la voiture", "Transport", 300,
                java.time.LocalDateTime.now().plusDays(15));

        p2.ajouterDepense(new Depense(nextDepenseId++, p2.getId(), "Vidange", 120, "Transport", "Huile et filtre"));
        p2.ajouterDepense(new Depense(nextDepenseId++, p2.getId(), "Pneus", 150, "Transport", "Pneus avant"));

        userPlannings.computeIfAbsent(1, k -> new ArrayList<>()).add(p2);

        // Planning 3: Études
        Planning p3 = new Planning(nextPlanningId++, 1, "Matériel Études",
                "Achats pour les études", "Éducation", 300,
                java.time.LocalDateTime.now().plusDays(45));

        p3.ajouterDepense(new Depense(nextDepenseId++, p3.getId(), "Livres", 80, "Éducation", "Livres scolaires"));
        p3.ajouterDepense(new Depense(nextDepenseId++, p3.getId(), "Fournitures", 50, "Éducation", "Cahiers, stylos"));

        userPlannings.computeIfAbsent(1, k -> new ArrayList<>()).add(p3);
    }

    // ✅ AJOUTEZ CETTE MÉTHODE
    public List<Planning> getAllPlannings() {
        List<Planning> allPlannings = new ArrayList<>();
        for (List<Planning> plannings : userPlannings.values()) {
            allPlannings.addAll(plannings);
        }
        return allPlannings;
    }

    public List<Planning> getPlanningsByUser(int userId) {
        return userPlannings.getOrDefault(userId, new ArrayList<>());
    }

    public Planning getPlanningById(int planningId) {
        for (List<Planning> plannings : userPlannings.values()) {
            for (Planning p : plannings) {
                if (p.getId() == planningId) {
                    return p;
                }
            }
        }
        return null;
    }

    public boolean addPlanning(int userId, Planning planning) {
        planning.setId(nextPlanningId++);
        planning.setUserId(userId);
        userPlannings.computeIfAbsent(userId, k -> new ArrayList<>()).add(planning);
        return true;
    }

    public boolean updatePlanning(Planning planning) {
        List<Planning> plannings = userPlannings.get(planning.getUserId());
        if (plannings != null) {
            for (int i = 0; i < plannings.size(); i++) {
                if (plannings.get(i).getId() == planning.getId()) {
                    plannings.set(i, planning);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deletePlanning(int userId, int planningId) {
        List<Planning> plannings = userPlannings.get(userId);
        if (plannings != null) {
            return plannings.removeIf(p -> p.getId() == planningId);
        }
        return false;
    }

    public boolean addDepense(int planningId, Depense depense) {
        Planning planning = getPlanningById(planningId);
        if (planning != null) {
            depense.setId(nextDepenseId++);
            depense.setPlanningId(planningId);
            planning.ajouterDepense(depense);
            return true;
        }
        return false;
    }

    public List<Depense> getDepensesByPlanning(int planningId) {
        Planning planning = getPlanningById(planningId);
        return planning != null ? planning.getDepenses() : new ArrayList<>();
    }

    public double getTotalDepensesByUser(int userId) {
        List<Planning> plannings = getPlanningsByUser(userId);
        return plannings.stream()
                .mapToDouble(Planning::getDepensesActuelles)
                .sum();
    }

    public double getTotalBudgetByUser(int userId) {
        List<Planning> plannings = getPlanningsByUser(userId);
        return plannings.stream()
                .mapToDouble(Planning::getBudgetTotal)
                .sum();
    }
}