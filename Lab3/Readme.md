# Java HTTP Requests

In this laboratory work I'm going to present a **way of performing HTTP requests in Java** — by using the built-in Java class HttpUrlConnection.  The site [httpbin.org](http://httpbin.org/) can be used for testing GET, POST, PUT, and DELETE http methods. Unfortunately other are not supported.

## HttpURLConnection  
The HttpUrlConnection class allows us to **perform basic HTTP requests without the use of any additional libraries**. All the classes that are needed are contained in the java.net package.

The disadvantages of using this method are that **the code can be more cumbersome than other HTTP libraries, and it does not provide more advanced functionalities such as dedicated methods for adding headers or authentication**.


### **1.  Creating a request**  
**A HttpUrlConnection instance is created by using the openConnection() method of the URL class**. This method only creates a connection object, but does not establish the connection yet.

The HttpUrlConnection class is used for all types of requests by setting the requestMethod attribute to one of the values: *GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE*.

```java
URL url = new URL("https://httpbin.org/get");
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");
```

### **2. Adding request parameters**  
To add parameters to a request, we have to set the doOutput property to true, then write a String of the form *param1=value&param2=value* to the OutputStream of the HttpUrlConnection instance:

```java
Map<String, String> parameters = new HashMap<>();
parameters.put("param1", "val");
 
con.setDoOutput(true);
DataOutputStream out = new DataOutputStream(con.getOutputStream());
out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
out.flush();
out.close();
```
To facilitate the transformation of the parameter Map, we have written a utility class called ParameterStringBuilder containing a static method getParamsString() that transforms a Map to a String of the required format:


```java 
public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) 
      throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
 
        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
        }
 
        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
} 
```
### **3. Setting request headers**  
Adding headers to a request can be achieved by using the setRequestProperty() method:

```java
con.setRequestProperty("Content-Type", "application/json");
```

To read the value of a header from a connection, we can use the getHeaderField() method:

```java
String contentType = con.getHeaderField("Content-Type");
```

### **4. Configuring time limits**  
HttpUrlConnection class allows **setting the connect and read timeouts**. These values define the interval of time to wait for the connection to the server to be established or data to be available for reading.

To set the timeout values we can use the setConnectTimeout() and setReadTimeout() methods:

```java
con.setConnectTimeout(5000);
con.setReadTimeout(5000);
```

### **5. Handling cookies**  
The java.net package contains classes that ease working with cookies such as CookieManager and HttpCookie.

First, to **read the cookies from a response**, we can retrieve the value of the Set-Cookie header and parse it to a list of HttpCookie objects:

```java
String cookiesHeader = con.getHeaderField("Set-Cookie");
List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
```

Next, we will **add the cookies to the cookie store**:

```java
cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));
```

Let’s check if a cookie called username is present, and if not, we will add it to the cookie store with a value of “john”:

```java
Optional<HttpCookie> usernameCookie = cookies.stream()
        .findAny().filter(cookie -> cookie.getName().equals("username"));
if (usernameCookie == null) {
    cookieManager.getCookieStore().add(null, new HttpCookie("username", "john"));
}
```

Finally, to **add the cookies to the request**, we need to set the Cookie header, after closing and reopening the connection:

```java
con.disconnect();
con = (HttpURLConnection) url.openConnection();
 
con.setRequestProperty("Cookie", 
        StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));
```

### **6. Reading response**  
Reading the response of the request can be done by **parsing the InputStream of the HttpUrlConnection instance**.

**To execute the request we can use the getResponseCode(), connect(), getInputStream() or getOutputStream() methods**:

```java
int status = con.getResponseCode();
```

Finally, let’s read the response of the request and place it in a content String:

```java
BufferedReader in = new BufferedReader(
  new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer content = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    content.append(inputLine);
}
in.close();
```

To **close the connection**, we can use the disconnect() method:

```java
con.disconnect();
```

## **Conclusions**  
An http request can be made using the steps above. HTTP protocol and it's methods are fundamental things in network programming, understanding those makes clear the whole image of data transfer.

### Main Class:

```java
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

```

Response for GET Request:

```
~ GET ~ http://httpbin.org/get | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 254
Date: Wed, 10 Apr 2019 12:05:56 GMT
Content-Type: application/json

{
  "args": {}, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_181"
  }, 
  "origin": "81.180.74.158, 81.180.74.158", 
  "url": "https://httpbin.org/get"
}
```

Response for POST Request:

```
~ POST ~ http://httpbin.org/post | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 452
Date: Wed, 10 Apr 2019 12:05:56 GMT
Content-Type: application/json

{
  "args": {}, 
  "data": "", 
  "files": {}, 
  "form": {
    "Param2": "val2", 
    "param1": "val1"
  }, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Content-Length": "23", 
    "Content-Type": "application/x-www-form-urlencoded", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_181"
  }, 
  "json": null, 
  "origin": "81.180.74.158, 81.180.74.158", 
  "url": "https://httpbin.org/post"
}
```

Response for PUT Request:

```
~ PUT ~ http://httpbin.org/put | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 361
Date: Wed, 10 Apr 2019 12:05:56 GMT
Content-Type: application/json

{
  "args": {}, 
  "data": "PutKey=PutValue", 
  "files": {}, 
  "form": {}, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Content-Length": "15", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_181"
  }, 
  "json": null, 
  "origin": "81.180.74.158, 81.180.74.158", 
  "url": "https://httpbin.org/put"
}
```

Response for DELETE Request:

```
~ DELETE ~ http://httpbin.org/delete | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 441
Date: Wed, 10 Apr 2019 12:05:57 GMT
Content-Type: application/json

{
  "args": {}, 
  "data": "", 
  "files": {}, 
  "form": {
    "DeleteKey": "DeleteValue"
  }, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Content-Length": "21", 
    "Content-Type": "application/x-www-form-urlencoded", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_181"
  }, 
  "json": null, 
  "origin": "81.180.74.158, 81.180.74.158", 
  "url": "https://httpbin.org/delete"
}
```

Response for PATCH Request:

```
~ PATCH ~ http://httpbin.org/patch | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 0
Access-Control-Max-Age: 3600
Date: Wed, 10 Apr 2019 12:05:57 GMT
Content-Type: text/html; charset=utf-8
Allow: PATCH, OPTIONS
```