package com.fw.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fw.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import com.fw.product.model.mysql.MyProduct;
import com.fw.product.model.postgre.PostProduct;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;



    @GetMapping("/product/mysql")
    public String getMysqlDate(@RequestParam(required = false) String param) {

        List<MyProduct> productList = productService.getProductListMysql(param);
        return productList.toString();
    }


    @GetMapping("/product/postgre")
    public String getPostgreDate(@RequestParam(required = false) String param) {
        List<PostProduct> productList = productService.getProductListPostgre(param);
        return productList.toString();
    }
    
    
}
