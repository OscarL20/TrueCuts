package e.l2040.truecuts;

import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    private String otherPersonsName;
    private String otherPersonsUid;
    private String chatRoomId;

    public ChatViewModel(String otherPersonsName, String otherPersonsUid, String chatRoomId) {
        this.otherPersonsName = otherPersonsName;
        this.otherPersonsUid = otherPersonsUid;
        this.chatRoomId = chatRoomId;
    }

    public ChatViewModel() {
    }

    public String getOtherPersonsName() {
        return otherPersonsName;
    }

    public void setOtherPersonsName(String otherPersonsName) {
        this.otherPersonsName = otherPersonsName;
    }

    public String getOtherPersonsUid() {
        return otherPersonsUid;
    }

    public void setOtherPersonsUid(String otherPersonsUid) {
        this.otherPersonsUid = otherPersonsUid;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
