package com.vector.okhttpusekotlindemo;

import java.io.IOException;

/**
 * Created by Vector
 * on 2016/11/9 0009.
 */

public class OneInterceptor implements MyInterceptor {
    private final static String TAG = OneInterceptor.class.getSimpleName();

    @Override
    public MyResponse intercept(Chain chain) throws IOException {
        MyRequest request = chain.request();
        request.setUrl(request.getUrl() + "-" + TAG);

        System.out.println(TAG + " : " + request.getUrl());

        MyResponse response = chain.proceed(request);

        response.setReponse(response.getReponse() + "-" + TAG);
        System.out.println(TAG + " : " + response.getReponse());

        return response;
    }
}
