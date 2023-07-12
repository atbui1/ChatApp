package com.edu.chatapp.api;

import com.edu.chatapp.repository.IClientService;

public class ApiClient extends ApiBase {

    public IApiService getApiServiceFCM() {
        return this.getService(IApiService.class, ApiUrl.BASE_URL_FCM);
    }

    public IApiService getApiServiceRealtime() {
        return this.getService(IApiService.class, ApiUrl.BASE_URL_REAL_TIME);
    }

    public IClientService iClientService() {
        return this.getService(IClientService.class, ApiUrl.BASE_URL_REAL_TIME);
    }
}
