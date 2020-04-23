package ${package}.common;

public class BaseErrorKeyConstants {

    private BaseErrorKeyConstants() {
    }

    /**
     * 4xx
     */
    public static final String BAD_REQUEST = "bad.request";
    public static final String PARAM_NULL = "param.null";
    public static final String PARAM_REQUIRED = "param.required";
    public static final String PARAM_REQUIRED_1 = "param.required.1";
    public static final String PARAM_INVALID = "param.invalid";
    public static final String PARAM_MULTIPART_REQUIRED = "param.multipart.required";
    public static final String OAUTH_UNAUTHORIZED = "oauth.unauthorized";
    public static final String RESOURCE_FORBIDDEN = "resource.forbidden";
    public static final String RESOURCE_VERSION_INCORRECT = "resource.version.incorrect";
    public static final String RESOURCE_VERSION_INVALID = "resource.version.invalid";
    public static final String API_NOT_FOUND = "api.not.found";
    public static final String MEDIA_TYPE_UNSUPPORTED = "media.type.unsupported";
    public static final String MEDIA_TYPE_NOTACCEPTABLE = "media.type.notacceptable";
    public static final String METHOD_NOT_SUPPORTED = "method.not.supported";
    public static final String TYPE_MISMATCH = "type.mismatch";
    public static final String USER_ID_INVALID = "user.id.invalid";

    public static final String CONFIG_NOT_EXIST = "config.not.exist";
    public static final String CONFIG_NOT_INTEGER = "config.not.integer";
    public static final String CONFIG_NOT_FLOAT = "config.not.float";
    public static final String SIGN_CHECK_ERROR = "sign.check.error";
    public static final String DECRYPT_ERROR = "decrypt.error";
    public static final String REQUEST_REPEAT = "request.repeat";
    public static final String JAQ_KEY_NOT_EXISTS = "jaq.key.not.exists";
    public static final String CLIENT_TIME_NOT_CURRENT = "client.time.not.current";
    public static final String SPRING_HTTP_NO_SUCH_REQUEST_HANDLING_METHOD = "spring.http.no-such-request-handling-method";
    public static final String SPRING_HTTP_REQUEST_METHOD_NOT_SUPPORTED = "spring.http.request-method-not-supported";
    public static final String FIELD_ERROR_1 = "field.error.1";
    public static final String FIELD_ERROR = "field.error";
    public static final String FILE_TOO_LARGE_1 = "file.too.large.1";


    /**
     * 5xx
     */
    public static final String RESOURCE_NOT_FOUND = "resource.not.found";
    public static final String DATABASE_ERROR = "database.error";
    public static final String UNKNOWN_ERROR = "unknown.error";
    public static final String UNKNOWN_ERROR_VALUE = "500;服务器发生未知错误.";
    public static final String BAD_API_ERROR = "bad.api.error";
    public static final String SERVICE_UNAVAILABLE = "service.unavailable";
}
