package ${package}.server.entity.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p></p>
 *
 * @author lixian
 * @date 2020-04-17
 */
@ApiModel("示例请求参数")
@Data
public class SimpleParam {

    @ApiModelProperty("姓名")
    @NotBlank(message = "请输入你的姓名")
    private String name;

    @ApiModelProperty("姓名")
    @NotNull(message = "请输入你的年龄")
    private Integer age;
}
