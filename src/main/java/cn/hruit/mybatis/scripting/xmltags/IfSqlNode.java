package cn.hruit.mybatis.scripting.xmltags;

/**
 * @author HONGRRY
 * @description IF SQL 节点
 * @date 2022/09/19 11:14
 **/
public class IfSqlNode implements SqlNode {

    private ExpressionEvaluator evaluator;
    private String test;
    private SqlNode contents;

    public IfSqlNode(SqlNode contents, String test) {
        this.test = test;
        this.contents = contents;
        this.evaluator = new ExpressionEvaluator();
    }

    @Override
    public boolean apply(DynamicContext context) {
        // 如果满足条件，则apply，并返回true
        if (evaluator.evaluateBoolean(test, context.getBindings())) {
            contents.apply(context);
            return true;
        }
        return false;
    }

}
