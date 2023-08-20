package zfani.assaf.radiokahollavan.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zfani.assaf.radiokahollavan.model.APIClient;
import zfani.assaf.radiokahollavan.model.APIInterface;
import zfani.assaf.radiokahollavan.model.Song;

public class AudioTrackViewModel extends AndroidViewModel {

    private static final String feed = "https://janus.shoutca.st/recentfeed/radiokahollavan/";
    private final MutableLiveData<List<Song>> audioTrack;

    public AudioTrackViewModel(@NonNull Application application) {
        super(application);
        audioTrack = new MutableLiveData<>();
    }

    public MutableLiveData<List<Song>> getAudioTrack() {
        APIClient.getClient(getApplication().getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE).getString("RecentlyPlayedUrl", feed)).create(APIInterface.class).getRecentFeed().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                JsonObject body = response.body();
                if (body != null && body.has("items")) {
                    JsonArray items = body.getAsJsonArray("items");
                    List<Song> songList = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        if (item.has("title") && item.has("date")) {
                            songList.add(new Song(item.get("title").getAsString(), item.get("date").getAsLong()));
                        }
                        Collections.sort(songList);
                        audioTrack.setValue(songList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
        return audioTrack;
    }
}
