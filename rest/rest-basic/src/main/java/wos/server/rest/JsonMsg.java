package wos.server.rest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

import java.io.Serializable;

public class JsonMsg<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean msgType;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 实例化JsonMsg对象
     *
     * @param t
     * @return
     */
    public static <T> JsonMsg<T> instance(T t) {
        JsonMsg<T> jsonMsg = new JsonMsg<T>(t);
        return jsonMsg;
    }

    /**
     * 实例化JsonMsg对象
     * @param t
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> JsonMsg<T> instance(T t,String msg) {
        JsonMsg<T> jsonMsg = new JsonMsg<T>(t);
        jsonMsg.setMsg(msg);
        return jsonMsg;
    }

    /**
     * 实例化JsonMsg对象
     * @param t
     * @param msgType
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> JsonMsg<T> instance(T t,boolean msgType,String msg) {
        JsonMsg<T> jsonMsg = new JsonMsg<T>(t);
        jsonMsg.setMsg(msg);
        jsonMsg.setMsgType(msgType);
        return jsonMsg;
    }

    /**
     * 获取错误JsonMsg对象
     * @param t
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> JsonMsg<T> getErrorInstance(T t,String msg) {
        JsonMsg<T> jsonMsg = new JsonMsg<T>(t);
        jsonMsg.setMsg(msg);
        jsonMsg.setMsgType(false);
        return jsonMsg;
    }

    /**
     * 错误
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> JsonMsg<T> error(String msg) {
        JsonMsg<T> jsonMsg = new JsonMsg<T>();
        jsonMsg.setMsg(msg);
        jsonMsg.setMsgType(false);
        return jsonMsg;
    }

    /**
     * 成功
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> JsonMsg<T> success(String msg) {
        JsonMsg<T> jsonMsg = new JsonMsg<T>();
        jsonMsg.setMsg(msg);
        jsonMsg.setMsgType(true);
        return jsonMsg;
    }


    public JsonMsg() {
        msgType = true;
    }

    public JsonMsg(T data) {
        msgType = true;
        this.data = data;
    }

    /**
     * 获取是否成功
     *
     * @return 是否成功
     */
    public boolean isMsgType() {
        return msgType;
    }

    /**
     * 设定是否成功
     *
     * @param msgType 是否成功
     */
    public void setMsgType(boolean msgType) {
        this.msgType = msgType;
    }

    /**
     * 获取消息
     *
     * @return 消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设定消息
     *
     * @param msg 消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设定数据
     *
     * @param data 数据
     */
    public void setData(T data) {
        this.data = data;
    }

    public static <T> JsonMsg<T> parse(String json) {
        return JSONObject.parseObject(json, new TypeReference<JsonMsg<T>>() {
        }, new Feature[0]);
    }

}