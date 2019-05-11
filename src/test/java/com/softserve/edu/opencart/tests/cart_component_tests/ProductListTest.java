package com.softserve.edu.opencart.tests.cart_component_tests;

import com.softserve.edu.opencart.data.IProduct;
import com.softserve.edu.opencart.data.ProductRepository;
import com.softserve.edu.opencart.pages.common.*;
import com.softserve.edu.opencart.pages.shop.CartProductComponent;
import com.softserve.edu.opencart.pages.shop.CartProductContainer;
import com.softserve.edu.opencart.tools.DataBaseUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Functional Testing")
@Feature("ProductListTest")
public class ProductListTest extends ATestRunner {

    @DataProvider
    public Object[][] productData() {
        return new Object[][] {
                {ProductRepository.getMacBook()},
                {ProductRepository.getIPhone()}
        };
    }

    @Description("Test Description: This test check whether the product has been added cart from homepage")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Click AddToCart and check if product added to Cart")
    @Test(dataProvider = "productData", description = "Add product to cart test")
    public void checkAddToCartButton(IProduct product) {
        log.debug("checkAddToCartButton test started");
        // Steps
        CartProductContainer cartProductContainer = loadApplication()
                .addProductToCart(product)
                .gotoHomePage()
                .openCartProductContainer();
        List<CartProductComponent> cartProductComponents = cartProductContainer
                .getCartProductComponents();
        // Check
        Assert.assertEquals(cartProductComponents.get(0).getCartProductNameText(),
                product.getName());
        // Step
        HomePage homePage = cartProductContainer
                .removeProductByName(product);
        log.debug("checkAddToCartButton test finished");
    }

    @DataProvider
    public Object[][] secondProductData() {
        return new Object[][] {
                {ProductRepository.getMacBook(), ProductRepository.getIPhone()}
        };
    }


    @Description("Test Description: This test check whether the product quantity has been increased" +
            "when we add some else product to cart")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Click AddToCart again and check if product quantity has increased")
    @Test(dataProvider = "secondProductData", description = "Increase quantity of items test")
    public void increaseQuantityOfItemsTest(IProduct firstProduct, IProduct secondProduct) {
        log.debug("increaseQuantityOfItemsTest test started");
        // Steps
        CartProductContainer cartProductContainer = loadApplication()
                .addProductToCart(firstProduct)
                .gotoHomePage()
                .addProductToCart(secondProduct)
                .gotoHomePage()
                .openCartProductContainer();
        List<CartProductComponent> cartProductComponents = cartProductContainer
                .getCartProductComponents();
        // Check
        Assert.assertEquals(cartProductComponents.get(1).getCartProductNameText(),
                firstProduct.getName());
        Assert.assertEquals(cartProductComponents.get(0).getCartProductNameText(),
                secondProduct.getName());
        // Steps
        cartProductContainer
                .removeProductByName(firstProduct)
                .openCartProductContainer()
                .removeProductByName(secondProduct);
        log.debug("increaseQuantityOfItemsTest test finished");

    }

    @DataProvider
    public Object[][] thirdProductData() {
        return new Object[][] {
                {ProductRepository.getMacBook(), "x 2"}
        };
    }

    @Description("This test checks if the same items are displayed in the cart correctly")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Add same product few times and check if quantity of same products is correct")
    @Test(dataProvider = "thirdProductData", description = "Add same product multiple times test")
    public void addSameItemMultipleTimesTest(IProduct product, String productQuantity) {
        log.debug("addSameItemMultipleTimesTest test started");
       // Steps
        CartProductContainer cartProductContainer = loadApplication()
                .addProductToCart(product)
                .gotoHomePage()
                .addProductToCart(product)
                .gotoHomePage()
                .openCartProductContainer();
        List<CartProductComponent> cartProductComponents = cartProductContainer
                .getCartProductComponents();
        // Check
        Assert.assertEquals(cartProductComponents.get(0).getCartProductNameText(),
                product.getName());
        Assert.assertEquals(cartProductComponents.get(0).getCartProductQuantityText(),
                productQuantity);
        // Steps
        cartProductContainer
                .removeProductByName(product);
        log.debug("addSameItemMultipleTimesTest test finished");

    }

    @DataProvider
    public Object[][] fourthProductData() {
        return new Object[][] {
                {ProductRepository.getMacBook()},
        };
    }

    @Description("This test checks if products add to cart from product page")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Add product to cart from product page")
    @Test(dataProvider = "fourthProductData", description = "Add product from product page test")
    public void addProductFromProductPageTest(IProduct product) {
        log.debug("addProductFromProductPageTest test started");
        // Steps
        CartProductContainer cartProductContainer = loadApplication()
                .clickProductName(product)
                .addProductToCart()
                .openCartProductContainer();
        List<CartProductComponent> cartProductComponents = cartProductContainer
                .getCartProductComponents();
        // Check
        Assert.assertEquals(cartProductComponents.get(0).getCartProductNameText(),
                product.getName());
        cartProductContainer
                .gotoHomePage()
                .openCartProductContainer()
                .removeProductByName(product);
        log.debug("addProductFromProductPageTest test finished");
    }

    @DataProvider
    public Object[][] fifthProductData() {
        return new Object[][] {
                {ProductRepository.getMacBook(), 100},
        };
    }

    @Description("This test checks if it is possible to add more items to the cart than it is in stock")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Add more products to cart than you have in the database")
    @Test(dataProvider = "fifthProductData", description = "Add more items then in DB test")
    // Steps
    public void addMoreItemsThenInDbTest(IProduct product, int addition) {
        log.debug("addMoreItemsThenInDbTest test started");
        CartProductContainer cartProductContainer = loadApplication()
                .clickProductName(product)
                .setQuantity(DataBaseUtils
                        .getProductQuantityFromDb(product) + addition)
                .addProductToCart()
                .gotoHomePage()
                .openCartProductContainer();
        List<CartProductComponent> cartProductComponents = cartProductContainer
                .getCartProductComponents();
        // Check
        Assert.assertTrue(cartProductComponents.size() == 0);
        // Steps
        cartProductContainer
                .removeProductByName(product);
        log.debug("addMoreItemsThenInDbTest test finished");
    }

}
