#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
package ${package}.server.filter;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import ${package}.common.BaseConstants;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>过滤器</p>
 *
 * @author lixian
 * @date 2020-04-23
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends MDCInsertingServletFilter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try {
            this.handleTid(servletRequest);
            super.doFilter(servletRequest, servletResponse, filterChain);
        } finally {
            MDC.clear();
        }
    }

    private void handleTid(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String tid = httpServletRequest.getHeader(BaseConstants.TRACKING_ID_HEADER);
            if (StringUtils.isEmpty(tid)) {
                tid = UUID.randomUUID().toString().replaceAll("-", "");
            }
            MDC.put(BaseConstants.MDC_TRACKING_ID, tid);
            // 预留tag使用
            String tag = httpServletRequest.getHeader(BaseConstants.TRACKING_TAG_HEADER);
            if (!StringUtils.isEmpty(tag)) {
                MDC.put(BaseConstants.TAG, tag);
            }
        }
    }


}
