package ${package}.server.service;

import ${package}.server.entity.req.SimpleParam;
import ${package}.server.entity.res.SimpleVO;
import org.springframework.stereotype.Service;

/**
 * <p>示例服务</p>
 *
 * @author lixian
 * @date 2020-04-22
 */
@Service
public class SimpleService {

    /**
     * 示例方法
     */
    public SimpleVO getSimple(SimpleParam param) {
        SimpleVO result = new SimpleVO();
        result.setAgeDesc(param.getAge() + "岁：~好年轻");
        result.setNameDesc(param.getName() + ": 好名字");
        return result;
    }
}