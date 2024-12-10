package com.fw.product.mapper.postgre;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.fw.product.model.postgre.PostProduct;

@Mapper
public interface ProductPostgreMapper {

    List<PostProduct> getProductListPostgre(String param);


    
}
