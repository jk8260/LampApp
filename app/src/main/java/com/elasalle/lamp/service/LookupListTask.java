package com.elasalle.lamp.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.FieldLookupListRepository;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.model.user.FieldLookupList;
import com.elasalle.lamp.model.user.LookupListItem;
import com.elasalle.lamp.security.TokenManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LookupListTask extends LampTask {

    private static final String TAG = LookupListTask.class.getSimpleName();

    private final OkHttpClient client;
    private final TokenManager tokenManager;
    private final FieldLookupListRepository repository;

    private List<Field> fields;

    @Inject
    public LookupListTask(@Nullable LampRestClient lampRestClient, @NonNull OkHttpClient client, TokenManager tokenManager, FieldLookupListRepository repository) {
        super(lampRestClient);
        this.client = client;
        this.tokenManager = tokenManager;
        this.repository = repository;
        this.fields = new ArrayList<>();
    }

    @Override
    public void run() {
        for (final Field field : fields) {
            client.newCall(new Request.Builder()
                    .header("token", tokenManager.getToken())
                    .url(field.getTarget())
                    .build())
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                ObjectMapper mapper = new ObjectMapper();
                                @SuppressWarnings("unchecked")
                                List<LookupListItem> lookupListItems = mapper.readValue(response.body().string(), new TypeReference<List<LookupListItem>>(){});
                                FieldLookupList fieldLookupList = new FieldLookupList(field);
                                fieldLookupList.setLookupListItems(lookupListItems);
                                repository.save(fieldLookupList);
                            } else {
                                ErrorMessage message = new ErrorMessage(response.body(), response.message());
                                Log.e(TAG, message.message);
                            }
                        }
                    });
        }
    }

    public void addField(Field field) {
        this.fields.add(field);
    }
}
