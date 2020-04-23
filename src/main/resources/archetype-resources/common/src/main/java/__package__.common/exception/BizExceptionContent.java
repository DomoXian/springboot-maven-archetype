package ${package}.common.exception;

import ${package}.common.BaseConstants;
import ${package}.common.BaseErrorKeyConstants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * <p>异常上下文</p>
 *
 * @author lixian
 * @date 2020-04-09
 */
@Data
public class BizExceptionContent implements Serializable {


    /**
     * http的状态码
     */
    private int httpStatus;

    /**
     * 全链路tid
     */
    private String tid;

    /**
     * 错误码定义
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;


    public BizExceptionContent(int httpStatus, String tid, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.tid = tid;
        this.code = code;
        this.message = message;
    }

    public BizExceptionContent() {
        this.httpStatus = 500;
        this.tid = MDC.get(BaseConstants.MDC_TRACKING_ID);
    }

    public BizExceptionContent(int httpStatus, String message) {
        this.tid = MDC.get(BaseConstants.MDC_TRACKING_ID);
        this.httpStatus = httpStatus;
        this.code = httpStatus;
        this.message = message;
    }

    public BizExceptionContent(String errorKey, Object... args) {
        ErrorKeyParser parser = ErrorKeyParser.inflateErrorKey(errorKey, args);
        this.tid = MDC.get(BaseConstants.MDC_TRACKING_ID);
        this.httpStatus = parser.getHttpStatus();
        this.code = parser.getCode();
        this.message = parser.getMessage();
    }

    private BizExceptionContent(BizExceptionContent response) {
        this.tid = response.getTid();
        this.httpStatus = response.getHttpStatus();
        this.code = response.getCode();
        this.message = response.getMessage();
    }


    public BizExceptionContent copy() {
        return new BizExceptionContent(this);
    }

    /**
     * 可以用这来获取异常体
     */
    public static BizExceptionContent getExceptionContext(String errorKey, Object... args) {
        if (ErrorKeyParser.getErrorMap().containsKey(errorKey)) {
            return new BizExceptionContent(errorKey, args);
        }
        return null;
    }


    /**
     * 错误码提取器
     */
    @Data
    private static class ErrorKeyParser {

        private static final Logger LOGGER = LoggerFactory.getLogger(ErrorKeyParser.class);

        private static Map<String, String> errorMap;

        private ErrorKeyParser() {

        }

        /**
         * http的状态码
         */
        private int httpStatus;

        /**
         * 错误码定义
         */
        private Integer code;

        /**
         * 错误提示
         */
        private String message;

        static {
            Map<String, String> map = new HashMap<>();

            // 默认加载Biz框架里面的错误码配置
            try (InputStream BizErrorFileStream = BizExceptionContent.class.getClassLoader()
                    .getResourceAsStream("Biz-error.properties")) {
                loadPropertyIfExist(BizErrorFileStream, map);
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }

            // 加载应用的error.properties配置
            try (InputStream errorFileStream = BizExceptionContent.class.getClassLoader()
                    .getResourceAsStream("error.properties")) {
                loadPropertyIfExist(errorFileStream, map);
            } catch (Exception e) {
                LOGGER.error("can't read customer error.properties", e);
            }
            map.putIfAbsent(BaseErrorKeyConstants.UNKNOWN_ERROR, BaseErrorKeyConstants.UNKNOWN_ERROR);
            errorMap = map;
        }

        static Map<String, String> getErrorMap() {
            if (errorMap == null) {
                errorMap = new HashMap<>(0);
            }
            return errorMap;
        }

        static ErrorKeyParser inflateErrorKey(String ek, Object... oargs) {
            ErrorKeyParser kp;
            if (ek == null) {
                LOGGER.warn("No error key is specified, we will use `unknown.error` by default");
                return inflateErrorKey(BaseErrorKeyConstants.UNKNOWN_ERROR);
            }

            if (oargs == null) {
                // prevent usage of: new BizExceptionContent("key", null)
                oargs = new Object[0];
            }

            String[] strArgs = Arrays.stream(oargs)
                    .map(String::valueOf)
                    .toArray(String[]::new);

            String fullMsg = errorMap.get(ek);
            if (StringUtils.isNotBlank(fullMsg)) {
                kp = inflateFullMessage(fullMsg, strArgs);
                return kp;
            } else {
                LOGGER.warn("Error key {} is not defined, we will use `unknown.error` by default", ek);
                return inflateErrorKey(BaseErrorKeyConstants.UNKNOWN_ERROR);
            }
        }

        private static void loadPropertyIfExist(InputStream inputStream, Map<String, String> map) throws IOException {
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    Properties errorProperties = new Properties();
                    errorProperties.load(reader);

                    for (String key : errorProperties.stringPropertyNames()) {
                        map.put(key, errorProperties.getProperty(key));
                    }
                }
            }
        }


        private static ErrorKeyParser inflateFullMessage(String fullMsg, String... args) {

            String[] msgArray = fullMsg.trim().split(";", -1);

            String reason = "";
            int httpCode = 500;
            int code = 500;

            String httpCodeStr = "500";
            String codeStr = "500";
            if (msgArray.length == 2) {
                codeStr = msgArray[0];
                httpCodeStr = msgArray[0];
                reason = msgArray[1];
            } else if (msgArray.length == 3) {
                codeStr = msgArray[1].toLowerCase();
                if (codeStr.startsWith("0x")) {
                    codeStr = Integer.toString(Integer.parseInt(codeStr.substring(2), 16));
                }
                httpCodeStr = msgArray[0];
                reason = msgArray[2];
            }
            // 处理占位符
            for (String arg : args) {
                if (arg != null) {
                    reason = StringUtils.replaceOnce(reason, "{}", arg);
                }
            }

            try {
                httpCode = Integer.parseInt(httpCodeStr);
                code = Integer.parseInt(codeStr);
            } catch (NumberFormatException e) {
                LOGGER.error("Bad error defined [{}], args:{}", fullMsg, args);
            }

            ErrorKeyParser errorKeyParser = new ErrorKeyParser();
            errorKeyParser.httpStatus = httpCode;
            errorKeyParser.code = code;
            errorKeyParser.message = reason;
            return errorKeyParser;
        }
    }

}
