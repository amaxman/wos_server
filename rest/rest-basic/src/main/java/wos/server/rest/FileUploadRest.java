package wos.server.rest;

public class FileUploadRest extends BasicRestEntity {
    //#region 静态变量
    public static String cachePath = "";
    //#endregion
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    private String fileMd5;

    private String fileExtension;

    private String fileEntityUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileEntityUrl() {
        return fileEntityUrl;
    }

    public void setFileEntityUrl(String fileEntityUrl) {
        this.fileEntityUrl = fileEntityUrl;
    }
}
