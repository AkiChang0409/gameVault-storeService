// src/main/java/com/gamevault/storeservice/service/discount/IDiscountStrategy.java
package com.gamevault.storeservice.service.discount;

import com.gamevault.storeservice.model.Game;
import java.math.BigDecimal;

public interface IDiscountStrategy {

    /**
     * 计算指定游戏的折扣金额
     */
    BigDecimal calculateDiscount(Game game, BigDecimal originalPrice);

    /**
     * 判断该折扣策略是否适用于某个游戏
     */
    boolean isApplicable(Game game);

    /**
     * 返回策略名称
     */
    String getStrategyName();
}
