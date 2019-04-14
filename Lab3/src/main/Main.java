package main;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        try {
            HttpMethods getRequest = new HttpMethods("http://httpbin.org/get", HTTPRequestTypes.GET);
            HttpMethods postRequest = new HttpMethods("http://httpbin.org/post", HTTPRequestTypes.POST)
                    .addParameter("param1", "val1")
                    .addParameter("Param2", "val2");
            HttpMethods putRequest = new HttpMethods("http://httpbin.org/put", HTTPRequestTypes.PUT)
                    .addParameter("PutKey", "PutValue");
            HttpMethods deleteRequest = new HttpMethods("http://httpbin.org/delete", HTTPRequestTypes.DELETE)
                    .addParameter("DeleteKey", "DeleteValue");
            HttpMethods patchRequest = new HttpMethods("http://httpbin.org/patch", HTTPRequestTypes.PATCH);
//            HttpMethods HeadRequest = new HttpMethods("http://httpbin.org/get", HTTPRequestTypes.HEAD);
//            HttpMethods OptionsRequest = new HttpMethods("http://httpbin.org/get", HTTPRequestTypes.OPTIONS);
//            HttpMethods TraceRequest = new HttpMethods("http://httpbin.org/get", HTTPRequestTypes.TRACE);

            RunHttpRequests(getRequest);
            RunHttpRequests(postRequest);
            RunHttpRequests(putRequest);
            RunHttpRequests(deleteRequest);
            RunHttpRequests(patchRequest);
//            testHttpMethods(HeadRequest);
//            testHttpMethods(OptionsRequest);
//            testHttpMethods(TraceRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void RunHttpRequests(HttpMethods HttpMethods) throws IOException {
        String httpResponse = HttpMethods.run();
        String responseBody = HttpMethods.getRequestBody();

        System.out.println(httpResponse + "\n" + responseBody);
    }
}