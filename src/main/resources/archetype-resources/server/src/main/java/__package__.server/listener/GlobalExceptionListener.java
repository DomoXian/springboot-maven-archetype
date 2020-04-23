package ${package}.server.listener;

import ${package}.common.dto.BizResult;

/**
 * <p>全局异常监听器，业务只需要实现该接口即可</p>
 *
 * @author lixian
 * @date 2020-04-21
 */
public interface GlobalExceptionListener {

    /**
     * 监听异常(业务可以在这个方法内实现异常监控)
     */
    void onListener(BizResult bizResult, Exception e);
}
