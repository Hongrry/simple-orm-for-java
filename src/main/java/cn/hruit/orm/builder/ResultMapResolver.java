package cn.hruit.orm.builder;

import cn.hruit.orm.mapping.ResultMapping;
import cn.hruit.orm.mapping.ResultMap;

import java.util.List;

/**
 * @author HONGRRY
 * @description 结果映射解析器
 * @date 2022/09/18 10:38
 **/
public class ResultMapResolver {

    private final MapperBuilderAssistant assistant;
    private String id;
    private Class<?> type;
    private List<ResultMapping> resultMappings;

    public ResultMapResolver(MapperBuilderAssistant assistant, String id, Class<?> type, List<ResultMapping> resultMappings) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.resultMappings = resultMappings;
    }

    public ResultMap resolve() {
        return assistant.addResultMap(this.id, this.type, this.resultMappings);
    }

}
