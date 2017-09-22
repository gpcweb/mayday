package cl.oktech.wood.services;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gabrielpoblete on 30-07-17.
 */

public interface PhotoService {

    @Multipart
    @POST("orders/{id}/upload")
    Call<ResponseBody> upload(@Path("id") int id,
                              @Query("token") String token,
                              @Part MultipartBody.Part attachment);
}
