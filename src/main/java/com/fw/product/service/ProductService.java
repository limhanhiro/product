package com.fw.product.service;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.fw.product.mapper.mysql.ProductMysqlMapper;
import com.fw.product.mapper.postgre.ProductPostgreMapper;
import com.fw.product.model.mysql.MyProduct;
import com.fw.product.model.postgre.PostProduct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductMysqlMapper productMysqlMapper;
    private final ProductPostgreMapper productPostgreMapper;


    public List<MyProduct> getProductListMysql(String param) {
        log.info("service start getProductListMysql");
        List<MyProduct> result = productMysqlMapper.getProductListMysql(param);
        log.info("service end getProductListMysql");
        return result;
        
    }

    public List<PostProduct> getProductListPostgre(String param) {
        log.info("service start getProductListPostgre");
        List<PostProduct> result =  productPostgreMapper.getProductListPostgre(param);
        log.info("service end getProductListPostgre");
        return result;
    }
    
}