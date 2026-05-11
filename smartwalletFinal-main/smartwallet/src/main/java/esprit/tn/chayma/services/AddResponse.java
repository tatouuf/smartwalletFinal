package esprit.tn.chayma.services;

public class AddResponse {
    private AddResult result;
    private String message;

    public AddResponse(AddResult result, String message) {
        this.result = result;
        this.message = message;
    }

    public AddResult getResult() { return result; }
    public String getMessage() { return message; }
}

