package com.vector.okhttpusekotlindemo;

import java.io.IOException;

/**
 * Created by Vector
 * on 2016/11/9 0009.
 */

public class NetInterceptor implements MyInterceptor {
    private final static String TAG = NetInterceptor.class.getSimpleName();

    @Override
    public MyResponse intercept(Chain chain) throws IOException {
        MyRequest request = chain.request();

        request.setUrl(request.getUrl() + "-" + TAG);

        System.out.println(TAG + " : " + request.getUrl());

        MyResponse response = new MyResponse();


        response.setReponse(request.getUrl() + " (this is response) ");

        response.setReponse(response.getReponse() + "-" + TAG);

        System.out.println(TAG + " : " + response.getReponse());

        return response;
    }
}
