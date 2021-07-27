package com.noriand.vo.request;

public class RequestGetActionHistoryVO extends RequestVO{
    public int userNo = 0;
    public int deviceNo = 0;
    public String fromDt = "";
    public String toDt = "";

    public RequestGetActionHistoryVO() {
        kind = "getActionHistory";
    }

    public String getParameter() {
        StringBuffer sb = new StringBuffer();
        sb.append("kind").append("=").append(kind).append("&");
        sb.append("userNo").append("=").append(userNo).append("&");
        sb.append("deviceNo").append("=").append(deviceNo).append("&");
        sb.append("fromDt").append("=").append(fromDt).append("&");
        sb.append("toDt").append("=").append(toDt).append("&");
        return sb.toString();
    }
}
