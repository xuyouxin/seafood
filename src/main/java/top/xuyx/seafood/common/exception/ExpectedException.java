package top.xuyx.seafood.common.exception;

import lombok.Getter;

@Getter
public class ExpectedException extends RuntimeException {
    private static final long serialVersionUID = 4061732202885069884L;
    /**
     * 错误码
     */
    private int code;
    /**
     * 错误描述
     */
    private String msg;
    /**
     * 自定义异常码和异常描述
     **/
    public ExpectedException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    /**
     * 通用异常码500，表示请求失败，自定义异常描述
     **/
    public ExpectedException(String msg) {
        super(msg);
        this.code = 500;
        this.msg = msg;
    }

    /**
     * 重写堆栈填充，不填充错误堆栈信息，提高性能
     * @return
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}