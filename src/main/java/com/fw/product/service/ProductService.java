package com.fw.product.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
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
        List<MyProduct> result = productMysqlMapper.getProductListMysql(param);
        return result;
        
    }

    public List<PostProduct> getProductListPostgre(String param) {
        List<PostProduct> result =  productPostgreMapper.getProductListPostgre(param);
        return result;
    }

    @Transactional
    public void insertProductPostgre() {
    
        productPostgreMapper.insertProductPostgre();
        productPostgreMapper.insertProductPostgre();
        productPostgreMapper.errorProductPostgre();
        
    }
    @Transactional("mysqlTransactionManager")
    public void insertProductMysql() {
        productMysqlMapper.insertProductMysql();
        productMysqlMapper.insertProductMysql();
        productMysqlMapper.errorProductMysql();

        
    }

    
}