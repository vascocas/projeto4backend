package paj.project4vc.exceptions;

public class TaskException extends Exception {
    private double amount;

    public TaskException(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

}
