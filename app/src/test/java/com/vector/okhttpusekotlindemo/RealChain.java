package com.vector.okhttpusekotlindemo;

import java.io.IOException;
import java.util.List;

/**
 * Created by Vector
 * on 2016/11/9 0009.
 */

public class RealChain implements MyInterceptor.Chain {
    private List<MyInterceptor> mMyInterceptors;
    private MyRequest mMyRequest;
    private final int index;
    private int calls;
    public RealChain(List<MyInterceptor> interceptors, int index,MyRequest request) {
        this.mMyInterceptors = interceptors;
        this.mMyRequest = request;
        this.index = index;
    }
    @Override
    public MyRequest request() {
        return mMyRequest;
    }

    @Override
    public MyResponse proceed(MyRequest request) throws IOException {

        if (index >= mMyInterceptors.size()) throw new AssertionError();


        RealChain realChain = new RealChain(mMyInterceptors, index + 1, mMyRequest);
        MyInterceptor myInterceptor = mMyInterceptors.get(index);
        MyResponse response = myInterceptor.intercept(realChain);

        if (response == null) {
            throw new NullPointerException("myInterceptor " + myInterceptor + " returned null");
        }

        return response;
    }

    @Override
    public MyConnection connection() {
        return null;
    }
}
