package com.tvhht.myapplication.api.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse{

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("data")
    @Expose
    private String data;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private String status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
