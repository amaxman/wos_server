package wos.server.rest.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import wos.server.entity.mobile.MobileFunc;
import wos.server.rest.BasicRestEntity;

@JsonIgnoreProperties({"id", "compId"})
public class UserPermRestEntity extends BasicRestEntity {
    private String title;
    private String code;
    private String imagePath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static UserPermRestEntity getInstance(MobileFunc mobileFunc) {
        if (mobileFunc==null) return null;
        UserPermRestEntity entity=new UserPermRestEntity();
        entity.setId(mobileFunc.getId());
        entity.setTitle(mobileFunc.getFuncTitle());
        entity.setCode(mobileFunc.getFuncCode());
        entity.setImagePath(mobileFunc.getImagePath());
        return entity;
    }
}
