package cl.oktech.wood;

/**
 * Created by cl on 04-07-17.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    public static final String BASE_URL = "https://private-c5bf8-mayday1.apiary-mock.com/api/v1/";
    private static Retrofit mRetrofit = null;


    public static Retrofit getClient() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
}