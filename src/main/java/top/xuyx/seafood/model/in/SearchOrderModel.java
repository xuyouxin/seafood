package top.xuyx.seafood.model.in;

import lombok.Data;

@Data
public class SearchOrderModel {
    //分页
    private Integer current;
    private Integer size;
    private Integer start;

    //查询条件
    private String userName;
    private Integer status;
    private String name;
    private String mobile;
    private String address;

}
