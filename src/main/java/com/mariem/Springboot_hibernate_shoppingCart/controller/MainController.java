package com.mariem.Springboot_hibernate_shoppingCart.controller;

import com.mariem.Springboot_hibernate_shoppingCart.Pagination.PaginationResult;
import com.mariem.Springboot_hibernate_shoppingCart.dao.OrderDao;
import com.mariem.Springboot_hibernate_shoppingCart.dao.ProductDao;
import com.mariem.Springboot_hibernate_shoppingCart.entity.Product;
import com.mariem.Springboot_hibernate_shoppingCart.form.CustomerForm;
import com.mariem.Springboot_hibernate_shoppingCart.model.CartInfo;
import com.mariem.Springboot_hibernate_shoppingCart.model.CustomerInfo;
import com.mariem.Springboot_hibernate_shoppingCart.model.ProductInfo;
import com.mariem.Springboot_hibernate_shoppingCart.utils.Utils;
import com.mariem.Springboot_hibernate_shoppingCart.validator.CustomerFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;

@Controller
@Transactional
public class MainController {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CustomerFormValidator customerFormValidator;

    @InitBinder
    public void myInitBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);
        if (target.getClass() == CartInfo.class) {
        } else if (target.getClass() == CustomerForm.class) {
            dataBinder.setValidator(customerFormValidator);
        }
    }

    @RequestMapping("/403")
    public String accessDenied() {
        return "/403";
    }

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping({"/productList"})
    public String listProductHandler(Model model, @RequestParam(value = "name", defaultValue = "") String likeName, @RequestParam(value = "page", defaultValue = "1") int page) {
        final int maxResult = 5;
        final int maxNavigationPage = 10;

        PaginationResult<ProductInfo> result = productDao.queryProducts(page, maxResult, maxNavigationPage, likeName);
        model.addAttribute("paginationProducts", result);
        return "productList";
    }

    @RequestMapping({"/buyProduct"})
    public String listProductHandler(HttpServletRequest request, Model model, @RequestParam(value = "code", defaultValue = "") String code) {
        Product product = null;
        if (code != null && code.length() > 0) {
            product = productDao.findProduct(code);
        }
        if (product != null) {
            CartInfo cartInfo = Utils.getCartINSession(request);
            ProductInfo productInfo = new ProductInfo(product);
            cartInfo.addProduct(productInfo, 1);
        }
        return "redirect:/shoppingCart";
    }


    @RequestMapping("/shoppingCartRemoveProduct")
    public String removeProductHandler(HttpServletRequest request, Model model, @RequestParam(value = "code", defaultValue = "") String code) {
        Product product = null;
        if (code != null && code.length() > 0) {
            product = productDao.findProduct(code);
        }
        if (product != null) {
            CartInfo cartInfo = Utils.getCartINSession(request);
            ProductInfo productInfo = new ProductInfo(product);
            cartInfo.removeProduct(productInfo);
        }
        return "redirect:/shoppingCart";
    }


    @RequestMapping(value = {"/shoppingCart"}, method = RequestMethod.POST)
    public String shoppingCartUpdateQty(HttpServletRequest request, Model model, @ModelAttribute("cartInfo") CartInfo cartForm) {
        CartInfo cartInfo = Utils.getCartINSession(request);
        cartInfo.updateQuantity(cartForm);
        return "redirect:/shoppingCart";
    }

    @RequestMapping(value = {"/shoppingCart"}, method = RequestMethod.GET)
    public String shoppingCartHandler(HttpServletRequest request, Model model) {
        CartInfo myCart = Utils.getCartINSession(request);
        model.addAttribute("cartForm", myCart);
        return "shoppingCart";
    }

    @RequestMapping(value = {"/shoppingCartCustomer"}, method = RequestMethod.GET)
    public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {
        CartInfo cartInfo = Utils.getCartINSession(request);
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        }
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        CustomerForm customerForm = new CustomerForm((customerInfo));
        model.addAttribute("customerForm", customerForm);
        return "shoppingCartCustomer";
    }

    @RequestMapping(value = {"/shoppingCartCustomer"}, method = RequestMethod.POST)
    public String shoppingCartCustomerSave(HttpServletRequest request, Model model, @ModelAttribute("customerForm") @Validated CustomerForm customerForm, BindingResult result, final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            customerForm.setValid(false);
            return "shoppingCartCustomer";
        }
        customerForm.setValid(true);
        CartInfo cartInfo = Utils.getCartINSession(request);
        CustomerInfo customerInfo = new CustomerInfo(customerForm);
        cartInfo.setCustomerInfo(customerInfo);
        return "redirect:/shoppingCartConfirmation";
    }

    @RequestMapping(value = {"/shoppingCartConfirmation"}, method = RequestMethod.GET)
    public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
        CartInfo cartInfo = Utils.getCartINSession(request);
        if (cartInfo == null || cartInfo.isEmpty()) {
            return "redirect:/shoppingCartCustomer";
        }
        model.addAttribute("myCart", cartInfo);
        return "shoppingCartConfirmation";
    }


    @RequestMapping(value = "/shoppingCartConfirmation", method = RequestMethod.POST)
    public String shoppingCartConfirmationSave(HttpServletRequest request, Model model) {
        CartInfo cartInfo = Utils.getCartINSession(request);
        if (cartInfo.isEmpty()) {
            return "redirect:/shoppingCart";
        }
        else if(!cartInfo.isValidCustomer()){
            return "redirect:/shoppingCartCustomer";
        }
        try{
            orderDao.saveOrder(cartInfo);
        }
        catch (Exception exception){
            return "shoppingCartConfirmation";
        }
        Utils.removeCartInSession(request);
        Utils.storeLastOrderedCartInSession(request,cartInfo);
        return "redirect:/shoppingCartFinalize";
    }


    @RequestMapping(value = {"/productImage"},method=RequestMethod.GET)
    public void productImage(HttpServletRequest request, HttpServletResponse response,Model model,@RequestParam("code")String code) throws IOException{
        Product product=null;
        if (code!=null){
            product=this.productDao.findProduct(code);
        }
        if (product!=null && product.getImage()!=null){
            response.setContentType("image/jpeg,image/jpg,image/png,image/gif");
            response.getOutputStream().write(product.getImage());
        }
        response.getOutputStream().close();
    }
}
