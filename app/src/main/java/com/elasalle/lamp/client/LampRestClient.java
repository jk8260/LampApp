package com.elasalle.lamp.client;

import com.elasalle.lamp.model.lookup.LookupResponse;
import com.elasalle.lamp.model.notification.Notification;
import com.elasalle.lamp.model.notification.NotificationUpload;
import com.elasalle.lamp.model.scan.ScanSetSync;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.guest.GuestLoginItems;
import com.elasalle.lamp.model.login.LoginCredentials;
import com.elasalle.lamp.model.login.LoginResponse;
import com.elasalle.lamp.model.search.SearchFilter;
import com.elasalle.lamp.model.search.SearchResponse;
import com.elasalle.lamp.model.Status;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LampRestClient {

    String URL_CONTEXT = "/mobilev2";
    String ABOUT = URL_CONTEXT +"/content/about";
    String AUTH = URL_CONTEXT + "/auth";
    String STATUS = URL_CONTEXT + "/status";
    String CUSTOMER_DETAILS = URL_CONTEXT + "/customers/details/{id}";
    String GUEST = URL_CONTEXT + "/guest";
    String RESET = URL_CONTEXT + "/account/forgotpassword/{username}";
    String SEARCH = URL_CONTEXT + "/search/{query}";
    String LOOKUP = URL_CONTEXT + "/assetlookup/{serial_number}";
    String SCAN_SET_SYNC = URL_CONTEXT + "/scanset/sync";
    String NOTIFICATIONS = URL_CONTEXT + "/notifications";
    String NOTIFICATIONS_UPDATE = URL_CONTEXT + "/notifications/update";
    String HEADER_ACCEPT_APPLICATION_JSON = "Accept: application/json";
    String HEADER_CONTENT_TYPE_APPLICATION_JSON = "Content-Type: application/json";
    String SEARCH_HELP = URL_CONTEXT +"/content/helpsearch";
    String LOOKUP_HELP = URL_CONTEXT +"/content/helplookup";
    String SCAN_HELP = URL_CONTEXT +"/content/helpscan";
    String NOTIFICATIONS_HELP = URL_CONTEXT +"/content/helpnotifications";

    @GET(ABOUT)
    Call<ResponseBody> about();

    @GET(SEARCH_HELP)
    Call<ResponseBody> searchHelp();

    @GET(LOOKUP_HELP)
    Call<ResponseBody> lookupHelp();

    @GET(SCAN_HELP)
    Call<ResponseBody> scanHelp();

    @GET(NOTIFICATIONS_HELP)
    Call<ResponseBody> notificationsHelp();

    @GET(AUTH)
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    Call<LoginResponse> login(@Header("Authorization") LoginCredentials credentials);

    @GET(STATUS)
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    Call<Status> status();

    @GET(CUSTOMER_DETAILS)
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    Call<Customer> customerDetails(@Header("Token") String token, @Path("id") String id);

    @POST(GUEST)
    @Headers(HEADER_CONTENT_TYPE_APPLICATION_JSON)
    Call<ResponseBody> guest(@Body GuestLoginItems guestLoginItems);

    @POST(RESET)
    @Headers(HEADER_ACCEPT_APPLICATION_JSON)
    Call<ResponseBody> resetPassword(@Path("username") String username);

    @POST(SEARCH)
    @Headers({
            HEADER_ACCEPT_APPLICATION_JSON,
            HEADER_CONTENT_TYPE_APPLICATION_JSON
    })
    Call<SearchResponse> search(@Header("Token") String token, @Header("location") String location, @Body SearchFilter filter, @Path("query") String query);

    @GET(LOOKUP)
    Call<LookupResponse> lookup(@Header("Token") String token, @Path("serial_number") String serialNumber);

    @POST(SCAN_SET_SYNC)
    Call<ScanSetSync> syncScanSet(@Header("Token") String token, @Body ScanSetSync scanSetSync);

    @GET(NOTIFICATIONS)
    Call<List<Notification>> getNotifications(@Header("Token") String token);

    @POST(NOTIFICATIONS_UPDATE)
    Call<ResponseBody> updateNotifications(@Header("Token") String token, @Body List<NotificationUpload> notificationUploads);

}
