package e.l2040.truecuts;

public class MetaDataChat {

    private String senderId;
    private String dateAndTime;
    private String otherPersonsImage;
    private String message;
    private String otherPersonsName;
    private String otherPersonsId;
    private String chatRoomId;

    public MetaDataChat(String senderId, String dateAndTime, String otherPersonsImage, String message, String otherPersonsName, String otherPersonsId, String chatRoomId) {
        this.senderId = senderId;
        this.dateAndTime = dateAndTime;
        this.otherPersonsImage = otherPersonsImage;
        this.message = message;
        this.otherPersonsName = otherPersonsName;
        this.otherPersonsId = otherPersonsId;
        this.chatRoomId = chatRoomId;
    }

    public MetaDataChat() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getOtherPersonsImage() {
        return otherPersonsImage;
    }

    public void setOtherPersonsImage(String otherPersonsImage) {
        this.otherPersonsImage = otherPersonsImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtherPersonsName() {
        return otherPersonsName;
    }

    public void setOtherPersonsName(String otherPersonsName) {
        this.otherPersonsName = otherPersonsName;
    }

    public String getOtherPersonsId() {
        return otherPersonsId;
    }

    public void setOtherPersonsId(String otherPersonsId) {
        this.otherPersonsId = otherPersonsId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
