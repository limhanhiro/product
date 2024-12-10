package com.fw.product.model.mysql;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyProduct {
    
    private Long id;
    private String name;
    private String price;
    private String createDate;
}
