package ${package}.common.dto;

import ${package}.common.BaseConstants;
import ${package}.common.exception.BizException;
import ${package}.common.exception.BizExceptionContent;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * <p>统一结果封装(适用于dubbo和http接口)</p>
 *
 * @author lixian
 * @date 2020-04-21
 */
@Data
public class BizResult<T> implements Serializable {

    /**
     * traceId
     */
    private String tid;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 提示
     */
    private String msg;

    /**
     * 响应结果
     */
    private T data;

    public static <T> BizResult<T> buildSuc(T data) {
        BizResult<T> result = new BizResult<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        result.setTid(MDC.get(BaseConstants.MDC_TRACKING_ID));
        return result;
    }

    public static BizResult buildFail(Integer code, String msg) {
        BizResult result = new BizResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setTid(MDC.get(BaseConstants.MDC_TRACKING_ID));
        return result;
    }


    public static BizResult buildFail(BizException e) {
        if (e == null || e.getExceptionContent() == null) {
            return buildFail(new BizExceptionContent(500, "unknown error"));
        }
        return buildFail(e.getExceptionContent());
    }

    public static BizResult buildFail(BizExceptionContent exceptionContent) {
        if (exceptionContent == null) {
            return buildFail(new BizExceptionContent(500, "unknown error"));
        }
        BizResult result = new BizResult();
        result.setCode(exceptionContent.getCode());
        result.setMsg(exceptionContent.getMessage());
        result.setTid(exceptionContent.getTid());
        return result;
    }
}
