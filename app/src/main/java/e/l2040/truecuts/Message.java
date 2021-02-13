package e.l2040.truecuts;

public class Message {

    String dateAndTime;
    String message;
    String senderId;

    public Message(String dateAndTime, String message, String senderId) {
        this.dateAndTime = dateAndTime;
        this.message = message;
        this.senderId = senderId;
    }

    public Message() {
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
