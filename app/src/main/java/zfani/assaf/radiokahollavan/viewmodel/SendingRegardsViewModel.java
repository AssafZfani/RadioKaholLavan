package zfani.assaf.radiokahollavan.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import zfani.assaf.radiokahollavan.R;

public class SendingRegardsViewModel extends ViewModel {

    private final MutableLiveData<Integer> text;

    public SendingRegardsViewModel() {
        text = new MutableLiveData<>(R.string.send_regards);
    }

    public LiveData<Integer> getText() {
        return text;
    }

    public void sendRegards(String name, String message) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("name", name);
        comment.put("comment", message);
        comment.put("sentDate", Timestamp.now());
        FirebaseFirestore.getInstance().collection("Comments").document().set(comment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                text.setValue(R.string.sent_regards);
            }
        });
    }
}