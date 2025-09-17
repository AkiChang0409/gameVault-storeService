package com.gamevault.storeservice.service;

import com.gamevault.storeservice.model.PurchasedGameActivationCode;
import com.gamevault.storeservice.model.UnusedGameActivationCode;
import com.gamevault.storeservice.repository.PurchasedGameActivationCodeRepository;
import com.gamevault.storeservice.repository.UnusedGameActivationCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GameActivationCodeService {

    private final UnusedGameActivationCodeRepository unusedRepo;
    private final PurchasedGameActivationCodeRepository purchasedRepo;

    public GameActivationCodeService(UnusedGameActivationCodeRepository unusedRepo,
                                     PurchasedGameActivationCodeRepository purchasedRepo) {
        this.unusedRepo = unusedRepo;
        this.purchasedRepo = purchasedRepo;
    }

    /** 游戏上架时生成初始激活码 */
    public void generateInitialCodes(Long gameId, int count) {
        generateCodes(gameId, count);
    }

    /** 库存不足时补充激活码 */
    public void replenishCodes(Long gameId, int count) {
        generateCodes(gameId, count);
    }

    private void generateCodes(Long gameId, int count) {
        List<UnusedGameActivationCode> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UnusedGameActivationCode code = new UnusedGameActivationCode();
            code.setGameId(gameId);
            code.setActivationCode(UUID.randomUUID().toString());
            codes.add(code);
        }
        unusedRepo.saveAll(codes);
    }

    /**
     * 结账时分配激活码（1个订单项 → 1个激活码）
     */
    @Transactional
    public PurchasedGameActivationCode assignCodeToOrderItem(Long userId, Long orderItemId, Long gameId) {
        // 取一个可用库存码
        UnusedGameActivationCode unused = unusedRepo.findFirstByGameId(gameId)
                .orElseThrow(() -> new IllegalStateException("该游戏没有可用激活码"));

        // 创建已购码
        PurchasedGameActivationCode purchased = PurchasedGameActivationCode.of(
                userId, orderItemId, gameId, unused.getActivationCode()
        );

        purchasedRepo.save(purchased);

        // 删除已使用库存码
        unusedRepo.delete(unused);

        return purchased;
    }
}
