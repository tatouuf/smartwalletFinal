package services;

import utils.AIConfig;
import java.util.*;
import java.util.logging.Logger;

public class AIService {

    private static final Logger logger = Logger.getLogger(AIService.class.getName());
    private static AIService instance;

    private AIService() {
        logger.info("AI Service initialized (LOCAL INTELLIGENCE MODE)");
    }

    public static synchronized AIService getInstance() {
        if (instance == null) {
            instance = new AIService();
        }
        return instance;
    }

    // ================= RECLAMATION CATEGORIZATION =================
    public String categorizeReclamation(String message) {

        if (message == null || message.trim().isEmpty())
            return "GENERAL";

        String m = message.toLowerCase();

        if (m.contains("payment") || m.contains("transaction") || m.contains("money") || m.contains("charge"))
            return "PAYMENT_ISSUE";

        if (m.contains("login") || m.contains("password") || m.contains("account") || m.contains("access"))
            return "ACCOUNT_ISSUE";

        if (m.contains("friend") || m.contains("block") || m.contains("request"))
            return "SOCIAL_ISSUE";

        if (m.contains("bug") || m.contains("error") || m.contains("crash") || m.contains("not working"))
            return "TECHNICAL_ISSUE";

        if (m.contains("refund") || m.contains("cancel") || m.contains("return"))
            return "REFUND_REQUEST";

        return "GENERAL";
    }

    // ================= SENTIMENT ANALYSIS =================
    public SentimentResult analyzeSentiment(String text){

        if(text == null || text.isEmpty())
            return new SentimentResult(0.5, "NEUTRAL", false);

        text = text.toLowerCase();

        String[] negative = {"bad","terrible","awful","hate","angry","worst","broken","fail"};
        String[] positive = {"good","great","excellent","love","amazing","perfect","thanks","nice"};
        String[] urgent   = {"urgent","asap","immediately","emergency","help","now"};

        int neg=0,pos=0;
        boolean urgentFlag=false;

        for(String w:negative) if(text.contains(w)) neg++;
        for(String w:positive) if(text.contains(w)) pos++;
        for(String w:urgent) if(text.contains(w)) urgentFlag=true;

        double score=0.5;
        String sentiment="NEUTRAL";

        if(pos>neg){
            score=0.7;
            sentiment="POSITIVE";
        }else if(neg>pos){
            score=0.3;
            sentiment="NEGATIVE";
            urgentFlag=true;
        }

        return new SentimentResult(score,sentiment,urgentFlag);
    }

    // ================= REPLY SUGGESTIONS =================
    public String generateReplySuggestion(String category,String userMessage){
        return getFallbackReply(category);
    }

    private String getFallbackReply(String category){
        return switch(category){
            case "PAYMENT_ISSUE" ->
                    "We received your payment issue. Our team is checking the transaction and will respond within 24 hours.";
            case "ACCOUNT_ISSUE" ->
                    "We detected an account access problem. Please verify your email and password. Support will assist you shortly.";
            case "TECHNICAL_ISSUE" ->
                    "We apologize for the technical issue. Our developers are investigating and a fix will be deployed soon.";
            case "REFUND_REQUEST" ->
                    "Your refund request is being processed. Please allow 5 business days for completion.";
            default ->
                    "Thank you for contacting SmartWallet support. We will review your request carefully.";
        };
    }

    // ================= FRAUD DETECTION =================
    public FraudAnalysis detectFraud(double amount,int dailyTransactions,List<Double> recentAmounts){

        double risk=0;
        List<String> reasons=new ArrayList<>();

        if(amount>5000){
            risk+=0.4;
            reasons.add("High transaction amount");
        }

        if(dailyTransactions>15){
            risk+=0.3;
            reasons.add("Too many transactions today");
        }

        if(recentAmounts!=null && !recentAmounts.isEmpty()){
            double avg=recentAmounts.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            if(amount>avg*3){
                risk+=0.3;
                reasons.add("Abnormal amount compared to history");
            }
        }

        String level = risk>=0.7?"HIGH":risk>=0.4?"MEDIUM":"LOW";

        return new FraudAnalysis(risk,level,reasons);
    }

    // ================= FRIEND SUGGESTION =================
    public List<Integer> suggestFriends(int userId,List<Integer> currentFriends,Map<Integer,List<Integer>> network){

        Map<Integer,Integer> mutual=new HashMap<>();

        for(int f:currentFriends){
            List<Integer> list=network.get(f);
            if(list!=null){
                for(int p:list){
                    if(p!=userId && !currentFriends.contains(p)){
                        mutual.merge(p,1,Integer::sum);
                    }
                }
            }
        }

        return mutual.entrySet().stream()
                .sorted((a,b)->b.getValue()-a.getValue())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    // ================= RESULT CLASSES =================
    public static class SentimentResult{
        private double score;
        private String sentiment;
        private boolean urgent;

        public SentimentResult(double s,String sen,boolean u){
            score=s; sentiment=sen; urgent=u;
        }

        public double getScore(){return score;}
        public String getSentiment(){return sentiment;}
        public boolean isUrgent(){return urgent;}
    }

    public static class FraudAnalysis{
        private double riskScore;
        private String riskLevel;
        private List<String> reasons;

        public FraudAnalysis(double r,String l,List<String> re){
            riskScore=r; riskLevel=l; reasons=re;
        }

        public double getRiskScore(){return riskScore;}
        public String getRiskLevel(){return riskLevel;}
        public List<String> getReasons(){return reasons;}
    }
}