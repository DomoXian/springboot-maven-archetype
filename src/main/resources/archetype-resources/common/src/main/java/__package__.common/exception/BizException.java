package ${package}.common.exception;

/**
 * <p>业务异常</p>
 *
 * @author lixian
 * @date 2020-04-08
 */
public class BizException extends RuntimeException {


    private final BizExceptionContent content;

    public BizException(String errorKey) {
        this(errorKey, new Object[0]);
    }

    /**
     * 使用定义的错误码key来构造响应异常
     */
    public BizException(String errorKey, Object... args) {
        this.content = new BizExceptionContent(errorKey, args);
    }

    /**
     * 使用一个现有的响应实体来构造异常
     */
    public BizException(BizExceptionContent content) {
        this.content = content.copy();
    }

    public BizExceptionContent getExceptionContent() {
        return content.copy();
    }

    @Override
    public String getMessage() {
        if (content == null) {
            return "";
        }
        return "{\"code\":" + content.getCode() + ",\"msg\":\"" + content.getMessage() + "\",\"tid\":\"" + content.getTid() + "\"}";

    }
}
