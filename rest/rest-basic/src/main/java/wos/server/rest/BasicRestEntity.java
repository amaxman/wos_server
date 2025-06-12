package wos.server.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BasicRestEntity {
    private String id;

    private String status;  //状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
