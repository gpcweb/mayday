package cl.oktech.wood.services;

/**
 * Created by cl on 04-07-17.
 */

import cl.oktech.wood.models.Provider;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ProviderService {
    @POST("log_in")
    Call<Provider> login(@Body Provider provider);

}