package ${package}.common;

import ch.qos.logback.classic.ClassicConstants;

/**
 * <p>常量</p>
 *
 * @author lixian
 * @date 2020-04-02
 */
public class BaseConstants extends ClassicConstants {


    private BaseConstants() {
    }

    public static final String TRACKING_ID_HEADER = "X-Tracking-ID";

    public static final String TRACKING_TAG_HEADER = "X-Tracking-Tag";


    public static final String TAG = "tag";
    public static final String MDC_TRACKING_ID = "tid";
}
