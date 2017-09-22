package cl.oktech.wood.services;

/**
 * Created by cl on 05-07-17.
 */

import java.util.List;

import cl.oktech.wood.models.Order;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface OrderService {
    @GET("orders")
    Call<List<Order>> getOrders(@Query("token") String token);

}