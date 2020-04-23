package ${package}.server.entity.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>示例返回参数</p>
 *
 * @author lixian
 * @date 2020-04-17
 */
@ApiModel("示例返回参数")
@Data
public class SimpleVO {

    @ApiModelProperty("姓名描述")
    private String nameDesc;

    @ApiModelProperty("年龄描述")
    private String ageDesc;
}
