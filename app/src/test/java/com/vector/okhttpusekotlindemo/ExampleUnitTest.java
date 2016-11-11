package com.vector.okhttpusekotlindemo;

import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSink;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .build();

    @Test
    public void syncGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        printResponseHeader(response);

        System.out.println(response.body().string());

        assertEquals(4, 2 + 2);

    }

    @Test
    public void asyncGet() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                printResponseHeader(response);

                System.out.println(response.body().string());

                assertEquals(4, 2 + 2);
            }
        });
    }

    @Test
    public void accessingHeaders() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        printResponseHeader(response);

        System.out.println(response.body().string());


        assertEquals(4, 2 + 2);
    }

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    @Test
    public void postString() throws Exception {
        String postBody = ""
                + "Releases\n"
                + "--------\n"
                + "\n"
                + " * _1.0_ May 6, 2013\n"
                + " * _1.1_ June 15, 2013\n"
                + " * _1.2_ August 11, 2013\n";

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());

        assertEquals(4, 2 + 2);
    }

    @Test
    public void postStream() throws Exception {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 997; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }

            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());

        assertEquals(4, 2 + 2);
    }

    @Test
    public void postFile() throws Exception {
        File file = new File("README.md");
        if (!file.exists()) {
            file.createNewFile();
        }

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());

        assertEquals(4, 2 + 2);
    }


    private void printResponseHeader(@Nullable Response response) {
        if (response == null) {
            return;
        }
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
    }

    @Test
    public void postParams() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
        assertEquals(4, 2 + 2);
    }

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Test
    public void cancelCall() throws Exception {
        Request request = new Request.Builder()
                .url("http://httpbin.org/delay/2") // This URL is served with a 2 second delay.
                .build();

        final long startNanos = System.nanoTime();
        final Call call = client.newCall(request);

        // Schedule a job to cancel the call in 1 second.
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.printf("%.2f Canceling call.%n", (System.nanoTime() - startNanos) / 1e9f);
                call.cancel();
                System.out.printf("%.2f Canceled call.%n", (System.nanoTime() - startNanos) / 1e9f);
            }
        }, 1, TimeUnit.SECONDS);

        try {
            System.out.printf("%.2f Executing call.%n", (System.nanoTime() - startNanos) / 1e9f);
            Response response = call.execute();
            System.out.printf("%.2f Call was expected to fail, but completed: %s%n", (System.nanoTime() - startNanos) / 1e9f, response);
        } catch (IOException e) {
            System.out.printf("%.2f Call failed as expected: %s%n", (System.nanoTime() - startNanos) / 1e9f, e);
        }
        assertEquals(4, 2 + 2);
    }

    @Test
    public void cloneClient() throws Exception {
        Request request = new Request.Builder()
                .url("http://httpbin.org/delay/1") // This URL is served with a 1 second delay.
                .build();

        try {
            // Copy to customize OkHttp for this request.
            OkHttpClient copy = client.newBuilder()
                    .readTimeout(500, TimeUnit.MILLISECONDS)
                    .build();

            Response response = copy.newCall(request).execute();
            System.out.println("Response 1 succeeded: " + response);
        } catch (IOException e) {
            System.out.println("Response 1 failed: " + e);
        }

        try {
            // Copy to customize OkHttp for this request.
            OkHttpClient copy = client.newBuilder()
                    .readTimeout(3000, TimeUnit.MILLISECONDS)
                    .build();

            Response response = copy.newCall(request).execute();
            System.out.println("Response 2 succeeded: " + response);
        } catch (IOException e) {
            System.out.println("Response 2 failed: " + e);
        }
    }

    private final OkHttpClient clientAuth = new OkHttpClient.Builder()
            .authenticator(new Authenticator() {
                @Override
                public Request authenticate(Route route, Response response) throws IOException {
                    System.out.println("Authenticating for response: " + response);
                    System.out.println("Challenges: " + response.challenges());
                    String credential = Credentials.basic("jesse", "password1");
                    if (credential.equals(response.request().header("Authorization"))) {
                        return null; // If we already failed with these credentials, don't retry.
                    }
                    if (responseCount(response) >= 3) {
                        return null; // If we've failed 3 times, give up.
                    }
                    return response.request().newBuilder()
                            .header("Authorization", credential)
                            .build();
                }

                private int responseCount(Response response) {
                    int result = 1;
                    while ((response = response.priorResponse()) != null) {
                        result++;
                    }
                    return result;
                }
            })
            .build();

    @Test
    public void auth() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/secrets/hellosecret.txt")
                .build();

        Response response = clientAuth.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());

        assertEquals(4, 2 + 2);
    }

    @Test
    public void getContentType() {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = "";

        try {
            contentType = fileNameMap.getContentTypeFor(URLEncoder.encode("test.jpeg", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println(contentType);
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {


        MediaType mediaType = MediaType.parse("application/json");

        System.out.println(mediaType.type()+"  "+mediaType.subtype()+"  "+mediaType.toString());

    }




}