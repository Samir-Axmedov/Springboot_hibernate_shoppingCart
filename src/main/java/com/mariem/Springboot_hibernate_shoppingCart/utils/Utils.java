package com.mariem.Springboot_hibernate_shoppingCart.utils;

import com.mariem.Springboot_hibernate_shoppingCart.model.CartInfo;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

public class Utils {

    public static CartInfo getCartINSession(HttpServletRequest request) {
    CartInfo cartinfo=(CartInfo) request.getSession().getAttribute("myCart");
    if (cartinfo==null){
        cartinfo=new CartInfo();
        request.getSession().setAttribute("myCart",cartinfo);
    }
    return cartinfo;
    }


    public static void removeCartInSession(HttpServletRequest request){
        request.getSession().removeAttribute("myCart");
    }

    public static void storeLastOrderedCartInSession(HttpServletRequest request,CartInfo cartInfo) {
        request.getSession().setAttribute("lastOrderedCart",cartInfo);
    }

    public static CartInfo getLastOrderedCartInSession(HttpServletRequest request){
        return (CartInfo) request.getSession().getAttribute("lastOrderedCart") ;
    }

}
