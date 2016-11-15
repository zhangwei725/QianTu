package com.zw.retrofitlib.exception;


import java.util.List;
import java.util.Map;

public class Config {
    private String isFormat;
    private ErrorBean error;
    private List<String> sucessCode;

    public String getIsFormat() {
        return isFormat;
    }

    public void setIsFormat(String isFormat) {
        this.isFormat = isFormat;
    }

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public List<String> getSucessCode() {
        return sucessCode;
    }

    public void setSucessCode(List<String> sucessCode) {
        this.sucessCode = sucessCode;
    }

    public static class ErrorBean {

        private Map<String, String> errorMap;

    }
}
