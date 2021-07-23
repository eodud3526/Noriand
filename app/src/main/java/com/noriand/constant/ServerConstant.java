package com.noriand.constant;

public class ServerConstant {
    public static final String BASE_URL = "http://3.37.126.139:8080/";

    public static final String APP_URL = "app.do";
    public static final String FILE_URL = "file.do";
    public static final String SEARCH_ADDRSS_URL = "searchAddressFromDaum.jsp";

    public static String getUrl(String subUrl) {
        return BASE_URL + subUrl;
    }
    public static String getMediaUrl(String subUrl) {
        return BASE_URL + "file/" + subUrl;
    }

    public static final String KAKAO_API_KEY = "022464c0ec3699f33660bd7862bbe810";
}
