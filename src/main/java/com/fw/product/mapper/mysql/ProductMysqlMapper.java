package com.fw.product.mapper.mysql;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.fw.product.model.mysql.MyProduct;

@Mapper
public interface ProductMysqlMapper {

    List<MyProduct> getProductListMysql(String param);


    
}
