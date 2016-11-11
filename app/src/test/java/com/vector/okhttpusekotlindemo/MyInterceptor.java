package com.vector.okhttpusekotlindemo;

import java.io.IOException;

/**
 * Created by Vector
 * on 2016/11/9 0009.
 */

public interface MyInterceptor {

    MyResponse intercept(MyInterceptor.Chain chain) throws IOException;

    interface Chain {
        MyRequest request();

        MyResponse proceed(MyRequest request) throws IOException;

        MyConnection connection();
    }
}
