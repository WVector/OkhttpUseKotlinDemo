package com.vector.okhttpusekotlindemo;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vector
 * on 2016/11/9 0009.
 */

public class RequestUnitTest {
    @Test
    public void test() throws IOException {

        MyRequest myRequest = new MyRequest();
        myRequest.setUrl(" (this is request) ");

        List<MyInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new OneInterceptor());
        interceptors.add(new TwoInterceptor());
        interceptors.add(new NetInterceptor());

        MyInterceptor.Chain chain = new RealChain(interceptors, 0, myRequest);

        MyResponse response = chain.proceed(myRequest);


        System.out.println(response.getReponse());
    }
}
