package cl.oktech.wood.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gabrielpoblete on 25-07-17.
 */

public class AppFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = AppFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        // Send token to app server
    }
}
