package com.mariem.Springboot_hibernate_shoppingCart.controller;


import com.mariem.Springboot_hibernate_shoppingCart.Pagination.PaginationResult;
import com.mariem.Springboot_hibernate_shoppingCart.dao.OrderDao;
import com.mariem.Springboot_hibernate_shoppingCart.dao.ProductDao;
import com.mariem.Springboot_hibernate_shoppingCart.entity.Product;
import com.mariem.Springboot_hibernate_shoppingCart.form.ProductForm;
import com.mariem.Springboot_hibernate_shoppingCart.model.OrderDetailInfo;
import com.mariem.Springboot_hibernate_shoppingCart.model.OrderInfo;
import com.mariem.Springboot_hibernate_shoppingCart.validator.ProductFormValidator;
import com.sun.istack.NotNull;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.util.List;

@Controller
@Transactional
public class AdminController {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDAO;

    @Autowired
    private ProductFormValidator productFormValidator;

    public void myInitBinder(WebDataBinder dataBinder) {
        Object target= dataBinder.getTarget() ;
        if (target==null) {
            return;
        }
        System.out.println("Target=" + target) ;
        if (target.getClass()== ProductForm.class){
            dataBinder.setValidator(productFormValidator);
        }
    }

    @RequestMapping (value={ "/admin/login"},method= RequestMethod.GET)
public String login(Model model)
    {
        return "login" ;
    }



    @RequestMapping(value={"/admin/accountInfo"},method=RequestMethod.GET)
    public String accountInfo(Model model){
        UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(userDetails.getPassword());
        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.isEnabled());

        model.addAttribute("UserDetails",userDetails);
        return "accountInfo";
    }



    @RequestMapping(value={"/admin/orderList"},method=RequestMethod.GET)
    public String orderList(Model model,//
    @RequestParam(value="page",defaultValue="1")String pageStr){
        int page=1;
        try {
            page = Integer.parseInt(pageStr);
        }
        catch(Exception e){}
        final int MAX_RESULT=5 ;
        final int MAX_NAVIGATION_PAGE=10;

        PaginationResult<OrderInfo> paginationResult=orderDao.listOrderInfo(page,MAX_RESULT,MAX_NAVIGATION_PAGE);
        model.addAttribute("paginationResult",paginationResult);
        return "orderList" ;
    }



    @RequestMapping(value={"/admin/product"},method=RequestMethod.GET)
    public String product(Model model, @RequestParam(value="code",defaultValue ="") String code){
        ProductForm productForm=null ;
        if (code!=null && code.length()>0) {
            Product product= productDAO.findProduct(code);
            if (product!=null) {
                productForm=new ProductForm(product) ;
            }
        }
        if (productForm==null) {
            productForm = new ProductForm();
            productForm.setNewProduct(true);
        }
        model.addAttribute("productForm",productForm);
        return "product" ;
    }



    @RequestMapping(value={"/admin/product"},method=RequestMethod.POST)
    public String productSave(Model model,
                              @ModelAttribute("productForm") @Validated ProductForm productForm, BindingResult result,
                              final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "product";
        }
        try {
            productDAO.save(productForm);
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            String message = rootCause.getMessage();
            model.addAttribute("errorMessage", message);
            return "product";
        }
        return "redirect:/productList";
    }



    @RequestMapping(value={"/admin/order"}, method= RequestMethod.GET)
    public String orderView(Model model,@RequestParam("orderId")String orderId) {
        OrderInfo orderInfo=null;
        if (orderId !=null) {
            orderInfo = this.orderDao.getOrderInfo(orderId);
        }
        if (orderInfo == null) {
            return "redirect:/admin/orderList";
        }
        List<OrderDetailInfo> details = this.orderDao.listOrderDetailInfos(orderId);
        orderInfo.setDetails(details);
        model.addAttribute("orderInfo",orderInfo) ;
        return "order";

    }
    }

