package x.demo.springcloud.microservice.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolResult<T> {
    //0 成功，非0 失败
    private int code;

    //响应提示信息
    private String message;

    //响应实体
    private T body;

    public ProtocolResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
