package io.github.serhiikorniiuk.mobile.api;

import io.github.serhiikorniiuk.mobile.api.models.Cart;
import io.github.serhiikorniiuk.mobile.api.models.CartRequest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.oneOf;
import static org.assertj.core.api.Assertions.within;

@Epic("API")
@Feature("Cart")
public class CartApiTest {

    @Test(groups = {"smoke", "api"}, description = "TC-API-011: New cart calculates totals correctly")
    @Severity(SeverityLevel.BLOCKER)
    public void cartTotalsAreCalculated() {
        CartRequest request = new CartRequest(1, List.of(
                new CartRequest.Item(1, 2),
                new CartRequest.Item(5, 1)));

        Cart cart = StoreApi.addCart(request)
                .then().statusCode(oneOf(200, 201)) // DummyJSON simulates creation, nothing is persisted
                .extract().as(Cart.class);

        assertThat(cart.userId()).isEqualTo(1);
        assertThat(cart.totalProducts()).isEqualTo(2);
        assertThat(cart.totalQuantity()).isEqualTo(3);
        assertThat(cart.products()).allSatisfy(line ->
                assertThat(line.total()).isCloseTo(line.price() * line.quantity(), within(0.01)));

        double expectedTotal = cart.products().stream().mapToDouble(Cart.Line::total).sum();
        assertThat(cart.total()).isCloseTo(expectedTotal, within(0.01));
    }
}
