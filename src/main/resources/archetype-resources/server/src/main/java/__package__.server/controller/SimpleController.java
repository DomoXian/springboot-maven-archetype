package ${package}.server.controller;

import ${package}.common.BaseConstants;
import ${package}.common.BaseErrorKeyConstants;
import ${package}.common.dto.BizResult;
import ${package}.common.exception.BizException;
import ${package}.server.ErrorKeyConstants;
import ${package}.server.entity.req.SimpleParam;
import ${package}.server.entity.res.SimpleVO;
import ${package}.server.service.SimpleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>实例controller</p>
 *
 * @author lixian
 * @date 2020-04-07
 */
@RestController
@Slf4j
@Api(tags = "示例接口")
public class SimpleController {


    @Autowired
    private SimpleService simpleService;


    @ApiImplicitParams({
            @ApiImplicitParam(name = BaseConstants.TRACKING_ID_HEADER, paramType = "header"),
            @ApiImplicitParam(name = BaseConstants.TRACKING_TAG_HEADER, paramType = "header")
    })
    @PostMapping("/hello")
    @ApiOperation("示例")
    public BizResult<SimpleVO> hello(@Validated @RequestBody SimpleParam param) {
        log.info("查看日志");
        if (param.getName().equals("base异常")) {
            throw new BizException(BaseErrorKeyConstants.BAD_API_ERROR);
        }
        if (param.getName().equals("业务异常")) {
            throw new BizException(ErrorKeyConstants.BIZ_ERROR, "大嘎达干");
        }
        return BizResult.buildSuc(simpleService.getSimple(param));
    }
}

