package org.example.Common;

public class RedisConstant {

    public static final String CART_KEY_PREFIX = "cart:";
    public static final long CART_TTL = 1800;

    public static final String MERCHANT_LIST_KEY = "merchant:valid:list";
    public static final long MERCHANT_TTL = 300;

    public static final String HOT_PRODUCTS_KEY = "hot:products:";
    public static final long HOT_TTL = 600;

    public static final String PRODUCT_DETAIL_KEY = "product:detail:";
    public static final long PRODUCT_TTL = 300;

    public static final String CATEGORY_LIST_KEY = "category:list:";
    public static final long CATEGORY_TTL = 600;
}