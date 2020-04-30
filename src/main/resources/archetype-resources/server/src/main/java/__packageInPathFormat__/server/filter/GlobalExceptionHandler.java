package ${package}.server.filter;

import ${package}.common.BaseErrorKeyConstants;
import ${package}.common.dto.BizResult;
import ${package}.common.exception.BizException;
import ${package}.common.exception.BizExceptionContent;
import ${package}.server.listener.GlobalExceptionListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * <p>全局异常处理</p>
 *
 * @author lixian
 * @date 2020-04-21
 */
@ControllerAdvice
@ConditionalOnProperty(value = "base.globalExceptionHandlerEnable")
@Slf4j
public class GlobalExceptionHandler {


    private static final String NOT_A_MULTIPART_REQUEST_MESSAGE = "Current request is not a multipart request";
    private static final char SEPARATOR_CHAR1 = '\'';
    private static final char SEPARATOR_CHAR2 = '\"';

    @Autowired
    private GlobalExceptionListener exceptionListener;

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public BizResult handleException(BizException e, HttpServletRequest request,
                                     HttpServletResponse response) {
        BizExceptionContent result = e.getExceptionContent();
        response.setStatus(result.getHttpStatus());

        if (result.getHttpStatus() < 500) {
            log.warn("request warn {}. '{}':'{}'", e.getClass().getSimpleName(), this.getPath(request), e.getMessage());
        } else {
            log.error(String.format("request error %s. '%s'", e.getClass().getSimpleName(), this.getPath(request)), e);
        }
        BizResult bizResult = BizResult.buildFail(result);
        exceptionListener.onListener(bizResult, e);
        return bizResult;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public BizResult handleException(MissingServletRequestParameterException e,
                                     HttpServletRequest request, HttpServletResponse response) {
        String message = e.getParameterName() + ":不能为空";
        return this.logClientError(e, request, response, BaseErrorKeyConstants.FIELD_ERROR, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public BizResult handleException(HttpMessageNotReadableException e, HttpServletRequest request,
                                     HttpServletResponse response) {
        return this.logClientError(e, request, response, BaseErrorKeyConstants.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public BizResult handleException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        BindingResult bindResult = e.getBindingResult();
        String message = getErrorMessage(bindResult.getAllErrors());
        return this.logClientError(e, request, response, BaseErrorKeyConstants.FIELD_ERROR_1, message);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BizResult> handleException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.warn("request {}. '{}':{}-'{}'", e.getClass().getSimpleName(), this.getPath(request), e.getContentType(), e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = e.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }
        BizExceptionContent result = new BizExceptionContent(BaseErrorKeyConstants.MEDIA_TYPE_UNSUPPORTED);
        BizResult bizResult = BizResult.buildFail(result);
        exceptionListener.onListener(bizResult, e);
        return new ResponseEntity<>(bizResult, headers, HttpStatus.valueOf(result.getHttpStatus()));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public BizResult handleException(HttpMediaTypeNotAcceptableException e,
                                     HttpServletRequest request, HttpServletResponse response) {
        return this.logClientError(e, request, response, BaseErrorKeyConstants.MEDIA_TYPE_NOTACCEPTABLE);
    }

    @ExceptionHandler({TypeMismatchException.class, ConversionNotSupportedException.class})
    @ResponseBody
    public BizResult handleTypeException(Exception e, HttpServletRequest request,
                                         HttpServletResponse response) {
        return this.logClientError(e, request, response, BaseErrorKeyConstants.TYPE_MISMATCH);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BizResult> handleException(HttpRequestMethodNotSupportedException e,
                                                     HttpServletRequest request) {
        log.warn("request {}. '{}':'{}'", e.getClass().getSimpleName(), this.getPath(request), e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        Set<HttpMethod> supportedMethods = e.getSupportedHttpMethods();
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
        }

        BizExceptionContent result = new BizExceptionContent(BaseErrorKeyConstants.SPRING_HTTP_REQUEST_METHOD_NOT_SUPPORTED);
        BizResult bizResult = BizResult.buildFail(result);
        exceptionListener.onListener(bizResult, e);
        return new ResponseEntity<>(bizResult, headers, HttpStatus.valueOf(result.getHttpStatus()));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public BizResult handleException(ServletRequestBindingException e, HttpServletRequest request, HttpServletResponse response) {
        String missing = this.resolveMissBindingParameter(e);
        if (StringUtils.isNotBlank(missing)) {
            return this.logClientError(e, request, response, BaseErrorKeyConstants.PARAM_INVALID, missing);
        } else {
            return this.logClientError(e, request, response, BaseErrorKeyConstants.BAD_REQUEST);
        }
    }

    private String resolveMissBindingParameter(ServletRequestBindingException ex) {
        String message = ex.getMessage();
        if (StringUtils.isBlank(message)) {
            return StringUtils.EMPTY;
        }

        int begin = message.indexOf(SEPARATOR_CHAR1);
        int end = message.lastIndexOf(SEPARATOR_CHAR1);

        if (begin != -1 && (begin + 1 < end)) {
            return message.substring(begin + 1, end);
        }

        begin = message.indexOf(SEPARATOR_CHAR2);
        end = message.lastIndexOf(SEPARATOR_CHAR2);
        if (begin != -1 && (begin + 1 < end)) {
            return message.substring(begin + 1, end);
        }

        return StringUtils.EMPTY;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public BizResult handleException(MissingServletRequestPartException e,
                                     HttpServletRequest request, HttpServletResponse response) {
        return this.logClientError(e, request, response, BaseErrorKeyConstants.FIELD_ERROR_1, e.getRequestPartName());
    }

    @ExceptionHandler({BindException.class})
    @ResponseBody
    public BizResult handleException(BindException e, HttpServletRequest request, HttpServletResponse response) {

        String message = getErrorMessage(e.getAllErrors());
        return this.logClientError(e, request, response, BaseErrorKeyConstants.FIELD_ERROR_1, message);

    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public BizResult handleException(NoHandlerFoundException e, HttpServletRequest request,
                                     HttpServletResponse response) {
        return this.logClientError(e, request, response, BaseErrorKeyConstants.API_NOT_FOUND);
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    @ResponseBody
    public BizResult handleException(AsyncRequestTimeoutException e,
                                     HttpServletRequest request, HttpServletResponse response) {
        if (response.isCommitted()) {
            if (log.isErrorEnabled()) {
                log.error("Async timeout for " + request.getMethod() + " [" + request.getRequestURI() + "]");
            }
            return null;
        }
        return this.logServerError(e, request, response, BaseErrorKeyConstants.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({Exception.class, MissingPathVariableException.class, HttpMessageNotWritableException.class})
    @ResponseBody
    public BizResult handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        return this.logServerError(e, request, response, BaseErrorKeyConstants.UNKNOWN_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public BizResult handleException(MultipartException e, HttpServletRequest request, HttpServletResponse response) {
        if (e.getCause() != null && e.getCause().getCause() instanceof SizeLimitExceededException) {
            Throwable cause = e.getCause().getCause();
            return this.logClientError(e, request, response, BaseErrorKeyConstants.FILE_TOO_LARGE_1, cause.getMessage());
        }

        if (e.getClass() == MultipartException.class &&
                NOT_A_MULTIPART_REQUEST_MESSAGE.equals(e.getMessage())) {
            return this.logClientError(e, request, response, BaseErrorKeyConstants.PARAM_MULTIPART_REQUIRED);
        }
        return this.logServerError(e, request, response, BaseErrorKeyConstants.UNKNOWN_ERROR);
    }

    private static String getErrorMessage(List<ObjectError> errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError error : errors) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errorMessage.append(fieldError.getField());
                errorMessage.append(":");
                errorMessage.append(fieldError.getDefaultMessage());
                errorMessage.append(",");
            }
        }
        String message = errorMessage.toString();
        if (errorMessage.length() > 0) {
            message = errorMessage.substring(0, errorMessage.length() - 1);
        }
        return message;
    }

    private String getPath(HttpServletRequest request) {
        return String.valueOf(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
    }

    private BizResult logClientError(Exception e, HttpServletRequest request,
                                     HttpServletResponse response, String errorKey, Object... args) {

        log.warn("request {}. '{}':'{}'", e.getClass().getSimpleName(), this.getPath(request), e.getMessage());
        BizResult BizResult = this.unifiedHandling(response, errorKey, args);
        exceptionListener.onListener(BizResult, e);
        return BizResult;
    }

    private BizResult logServerError(Exception e, HttpServletRequest request, HttpServletResponse response, String errorKey, Object... args) {
        log.error(String.format("request %s. '%s'", e.getClass().getSimpleName(), this.getPath(request)), e);
        BizResult BizResult =  this.unifiedHandling(response, errorKey, args);
        exceptionListener.onListener(BizResult, e);
        return BizResult;
    }

    private BizResult unifiedHandling(HttpServletResponse response, String errorKey, Object... args) {
        BizExceptionContent exceptionResponse = new BizExceptionContent(errorKey, args);
        response.setStatus(exceptionResponse.getHttpStatus());
        return BizResult.buildFail(exceptionResponse);
    }

}