package top.xuyx.seafood.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.xuyx.seafood.common.enums.StatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Integer code;
    private String message;
    private Object data;

    public static Response ok(Object data) {
        return new Response(StatusEnum.code_200.getCode(), StatusEnum.code_200.getMessage(), data);
    }

    public static Response ok() {
        return new Response(StatusEnum.code_200.getCode(), StatusEnum.code_200.getMessage(), null);
    }

    public static Response fail(StatusEnum status) {
        return new Response(status.getCode(), status.getMessage(), null);
    }

	public static Response fail(String message) {
        return new Response(StatusEnum.code_201.getCode(), message, null);
    }

}
