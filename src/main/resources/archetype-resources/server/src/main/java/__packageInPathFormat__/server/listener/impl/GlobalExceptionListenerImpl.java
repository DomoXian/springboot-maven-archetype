package ${package}.server.listener;

import ${package}.common.dto.BizResult;
import ${package}.server.listener.GlobalExceptionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>全局异常监听</p>
 *
 * @author lixian
 * @date 2020-04-21
 */
@Service
@Slf4j
public class GlobalExceptionListenerImpl implements GlobalExceptionListener {

    @Override
    public void onListener(BizResult bizResult, Exception e) {
        // TODO: 21/4/20  这里实现监听动作
    }
}
