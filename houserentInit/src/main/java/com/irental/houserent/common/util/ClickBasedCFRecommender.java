package com.irental.houserent.common.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * 推荐算法工具类
 */
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于用户行为的协同过滤推荐静态工具类
 */
public final class ClickBasedCFRecommender {

    // 私有构造器防止实例化
    private ClickBasedCFRecommender() {}

    // 用户-物品点击矩阵: userId -> (itemId -> clickCount)
    private static final Map<Long, Map<Long, Integer>> USER_ITEM_CLICK_MATRIX = new HashMap<>();

    // 物品相似度缓存
    private static final Map<Long, Map<Long, Double>> ITEM_SIMILARITY_CACHE = new HashMap<>();

    /**
     * 添加用户点击记录
     * @param userId 用户ID
     * @param itemId 物品ID
     */
    public static synchronized void addClickRecord(Long userId, Long itemId) {
        USER_ITEM_CLICK_MATRIX.computeIfAbsent(userId, k -> new HashMap<>())
                .merge(itemId, 1, Integer::sum);
        clearSimilarityCache(); // 添加新记录后清空相似度缓存
    }

    /**
     * 批量添加点击记录
     * @param records 点击记录列表，每个记录是[userId, itemId]数组
     */
    public static synchronized void addClickRecords(List<Long[]> records) {
        for (Long[] record : records) {
            addClickRecord(record[0], record[1]);
        }
    }

    /**
     * 为用户生成推荐物品
     * @param userId 目标用户ID
     * @param topN 返回推荐物品数量
     * @return 推荐物品ID列表，按得分从高到低排序
     */
    public static synchronized List<Long> recommendItems(Long userId, int topN) {
        if (!USER_ITEM_CLICK_MATRIX.containsKey(userId)) {
            return Collections.emptyList();
        }

        // 获取目标用户点击过的物品
        Set<Long> clickedItems = USER_ITEM_CLICK_MATRIX.get(userId).keySet();

        // 计算候选物品的推荐得分
        Map<Long, Double> itemScores = new HashMap<>();

        for (Long clickedItem : clickedItems) {
            // 获取与当前点击物品相似的其他物品
            Map<Long, Double> similarItems = getItemSimilarities(clickedItem);

            for (Map.Entry<Long, Double> entry : similarItems.entrySet()) {
                Long candidateItem = entry.getKey();
                double similarity = entry.getValue();

                // 跳过用户已经点击过的物品
                if (!clickedItems.contains(candidateItem)) {
                    // 加权得分 = 相似度 * 用户对原物品的点击次数
                    double weightedScore = similarity * USER_ITEM_CLICK_MATRIX.get(userId).get(clickedItem);
                    itemScores.merge(candidateItem, weightedScore, Double::sum);
                }
            }
        }

        // 按得分排序并返回topN
        return itemScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 计算物品之间的余弦相似度
     */
    private static double calculateItemSimilarity(Long itemId1, Long itemId2) {
        if (itemId1.equals(itemId2)) {
            return 1.0;
        }

        // 获取同时点击过这两个物品的用户
        Set<Long> commonUsers = getUsersWhoClickedItem(itemId1).stream()
                .filter(userId -> getUsersWhoClickedItem(itemId2).contains(userId))
                .collect(Collectors.toSet());

        if (commonUsers.isEmpty()) {
            return 0.0;
        }

        // 计算余弦相似度
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Long userId : commonUsers) {
            int clicks1 = USER_ITEM_CLICK_MATRIX.get(userId).get(itemId1);
            int clicks2 = USER_ITEM_CLICK_MATRIX.get(userId).get(itemId2);

            dotProduct += clicks1 * clicks2;
            norm1 += clicks1 * clicks1;
            norm2 += clicks2 * clicks2;
        }

        return (norm1 == 0 || norm2 == 0) ? 0.0 : dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 获取与指定物品相似的其他物品
     */
    private static Map<Long, Double> getItemSimilarities(Long itemId) {
        // 先从缓存中查找
        if (ITEM_SIMILARITY_CACHE.containsKey(itemId)) {
            return ITEM_SIMILARITY_CACHE.get(itemId);
        }

        // 计算所有物品与当前物品的相似度
        Map<Long, Double> similarities = new HashMap<>();
        Set<Long> allItems = getAllItems();

        for (Long otherItem : allItems) {
            if (!otherItem.equals(itemId)) {
                double similarity = calculateItemSimilarity(itemId, otherItem);
                if (similarity > 0) {
                    similarities.put(otherItem, similarity);
                }
            }
        }

        // 放入缓存
        ITEM_SIMILARITY_CACHE.put(itemId, similarities);
        return similarities;
    }

    /**
     * 获取点击过某物品的所有用户
     */
    private static Set<Long> getUsersWhoClickedItem(Long itemId) {
        return USER_ITEM_CLICK_MATRIX.entrySet().stream()
                .filter(entry -> entry.getValue().containsKey(itemId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * 获取系统中所有物品
     */
    private static Set<Long> getAllItems() {
        return USER_ITEM_CLICK_MATRIX.values().stream()
                .flatMap(itemMap -> itemMap.keySet().stream())
                .collect(Collectors.toSet());
    }

    /**
     * 清空相似度缓存
     */
    public static synchronized void clearSimilarityCache() {
        ITEM_SIMILARITY_CACHE.clear();
    }

    /**
     * 清空所有数据（重置推荐器）
     */
    public static synchronized void reset() {
        USER_ITEM_CLICK_MATRIX.clear();
        ITEM_SIMILARITY_CACHE.clear();
    }

    /**
     * 获取当前数据统计信息（用于监控）
     */
    public static synchronized String getStats() {
        return String.format(
                "Users: %d, Items: %d, SimilarityCacheSize: %d",
                USER_ITEM_CLICK_MATRIX.size(),
                getAllItems().size(),
                ITEM_SIMILARITY_CACHE.size()
        );
    }
}