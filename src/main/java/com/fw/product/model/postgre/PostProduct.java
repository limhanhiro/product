package com.fw.product.model.postgre;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostProduct {
    
    private Long id;
    private String name;
    private String price;
    private String createDate;
}
