package me.jiangcai.poi.template;

import lombok.Data;
import me.jiangcai.poi.template.service.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 可认定唯一的key
 * 特定我们的集合元素是非常复杂的；
 * 而且每个元素都可能需要认定唯一（即允许给出重复数据）
 *
 * @author CJ
 */
@Data
public class EqualsKey {

    /**
     * 仅提供构造支持
     */
    @Data
    public static class SubEqualsKeyPair {
        private final String name;
        private final EqualsKey key;
    }

    public static EqualsKey valueOf(String... keys) {
        return new EqualsKey(keys, null);
    }

    public static EqualsKey valueOf(String[] keys, SubEqualsKeyPair... subKeys) {
        return new EqualsKey(keys, Stream.of(subKeys)
                .collect(Collectors.toMap(SubEqualsKeyPair::getName, SubEqualsKeyPair::getKey)));
    }

    private final String[] keys;
    private final Map<String, EqualsKey> childKeys;

    public List<Map<String, Cell>> build(List<Map<String, Cell>> list) {
        return build(null, list);
    }

    private List<Map<String, Cell>> build(String originName, List<Map<String, Cell>> list) {
        if (keys == null || keys.length == 0)
            return list;
        Set<String> equalsKeys = Stream.of(keys)
                .map(src -> originName == null ? src : originName + "." + src)
                .collect(Collectors.toSet());

        List<Integer> hashes;
        if (originName != null) {
            equalsKeys.add(originName);
            // 第一步回购原信息
            flatRow(list, equalsKeys);

            hashes = getHash(list, equalsKeys);

            order(list, hashes, equalsKeys);
            hashes = getHash(list, equalsKeys);
        } else {
            // 作为整列数据 并无重新排序的必要
            hashes = getHash(list, equalsKeys);
        }


        Map<String, Cell> lastHitRow = null;
        Integer lastHash = Integer.MAX_VALUE;

        List<Map<String, Cell>> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Map<String, Cell> row = list.get(i);
            if (lastHitRow != null && lastHash.intValue() == hashes.get(i)) {
                // 命中 我们就叠加吧
                for (String key : equalsKeys) {
                    Cell cell = lastHitRow.get(key);
                    final Cell currentCell = row.get(key);
                    cell.setRows(cell.getRows() + currentCell.getRows());
                    currentCell.setRows(0);
                }
            } else if (validRow(row, equalsKeys)) {
                // 更换的前提是当前行是有效的 即所有rows>0
                lastHitRow = row;
                lastHash = hashes.get(i);
            }
            result.add(row);
        }
        // 整合完成、 现在进入逐个
        // 把相同的列拉出来 期望的结果是int[] 一个表示开始索引 一个表示长度
        @SuppressWarnings("unchecked")
        Map<String, Cell>[] array = new Map[result.size()];
        result.toArray(array);
        for (int i = 0; i < array.length; i++) {
            Map<String, Cell> current = array[i];
            if (validRow(current, equalsKeys)) {
                // 该行有效 那么取它的rows即为跨行数
                int span = rowSpan(current, equalsKeys);
                // 我要的就是 i-(i+span-1)
                if (childKeys != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Cell>[] subArray = new Map[span];
                    System.arraycopy(array, i, subArray, 0, span);
                    // 准备拉出来处理的列
                    List<Map<String, Cell>> subList = Arrays.asList(subArray);
                    // originName.name
                    for (String subKey : childKeys.keySet()) {
                        String distName;
                        if (originName == null) {
                            distName = subKey;
                        } else
                            distName = originName + "." + subKey;
                        subList = childKeys.get(subKey).build(distName, subList);
                    }
                    // 弄回array
                    for (int j = 0; j < subList.size(); j++) {
                        array[i + j] = subList.get(j);
                    }
                }
                i += span - 1;
            }
        }
        return Arrays.asList(array);
    }

    private void order(List<Map<String, Cell>> list, List<Integer> hashes, Set<String> equalsKeys) {
        List<Map<String, Cell>> newList = order(list, hashes);
        //
        List<Map<String,Cell>> mergeList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

//            Map<String, Cell> origin = list.get(i);
            Map<String, Cell> dist = newList.get(i);
            final Map<String, Cell> element = new HashMap<>();
            mergeList.add(i, element);
            equalsKeys.forEach(key -> element.put(key,dist.get(key)));
//            equalsKeys.forEach(key -> origin.put(key, dist.get(key)));
        }
        for (int i = 0; i < list.size(); i++) {
            Map<String, Cell> origin = list.get(i);
            final Map<String, Cell> element = mergeList.get(i);
            equalsKeys.forEach(key -> origin.put(key,element.get(key)));
        }
    }

    private void flatRow(List<Map<String, Cell>> list, Set<String> equalsKeys) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Cell> map = list.get(i);
            for (String key : equalsKeys) {
                if (map.containsKey(key) && map.get(key).getRows() > 1) {
                    // 将值设置给其后的几个map 并且让自己也为1
                    final Cell cell = map.get(key);
                    int rows = cell.getRows();
                    for (int j = 0; j < rows; j++) {
                        final Cell cell1 = list.get(i + j).get(key);
                        cell1.setRows(1);
                        cell1.setValue(cell.getValue());
                    }
                }
            }
        }
    }

    private List<Integer> getHash(List<Map<String, Cell>> list, Set<String> equalsKeys) {
        List<Integer> hashes = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Map<String, Cell> map = list.get(i);
            Object[] data = new Object[equalsKeys.size()];
            int j = 0;
            for (String key : equalsKeys) {
                final Cell cell = map.get(key);
                if (cell != null) {
                    data[j] = cell.getValue();
//                                    if (data[j]!=null && data[j] instanceof ValueNode){
//                                        data[j] = data[j].toString();
//                                    }
                }
                j++;
            }
            hashes.add(Objects.hash(data));
        }
        return hashes;
    }

    private List<Map<String, Cell>> order(List<Map<String, Cell>> list, List<Integer> hashes) {
        // 根据原顺序
        int[] indexes = new int[list.size()];
        Arrays.setAll(indexes, operand -> -1);
        Integer currentHash = null;
        while (true) {
            // i 这个里的语意是要决定 indexes 的索引 而非list的index
            // 决定是迁移还是怎么滴
            // 寻找一个未被使用的i
            int i = -1;
            for (int j = 0; j < indexes.length; j++) {
                if (indexes[j] == -1) {
                    i = j;
                    break;
                }
            }
            if (i == -1)
                break;
            //  寻找要用的list元素 indexes 里面尚未被用到的第一个index
            int targetListIndex = -1;
            for (int j = 0; j < list.size(); j++) {
                // 要是j 没有被用过 则就j
                boolean meet = false;
                for (int target : indexes) {
                    if (target == j) {
                        meet = true;
                        break;
                    }
                }
                if (!meet) {
                    targetListIndex = j;
                    break;
                }
            }

            if (currentHash == null || hashes.get(targetListIndex).intValue() != currentHash) {
                // 那就换
                currentHash = hashes.get(targetListIndex);
                // 将同值的 都拿过去
                indexes[i] = targetListIndex;
                for (int j = 0; j < hashes.size(); j++) {
                    if (hashes.get(j).equals(currentHash) && j != targetListIndex) {
                        indexes[++i] = j;
                    }
                }
                //
            }
        }
        List<Map<String, Cell>> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (indexes[j] == i) {
                    newList.add(list.get(j));
                    break;
                }
            }
        }
//        @SuppressWarnings("unchecked")
//        Map<String,Cell>[] array = new Map[list.size()];

        return newList;
    }

    private int rowSpan(Map<String, Cell> row, Set<String> equalsKeys) {
        return row.entrySet().stream()
                .filter(stringCellEntry -> equalsKeys.contains(stringCellEntry.getKey()))
                .map(Map.Entry::getValue)
                .map(Cell::getRows)
                .filter(integer -> integer > 0)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("不是有效的么？"));
    }

    /**
     * @param row
     * @param equalsKeys
     * @return 有效的行
     */
    private boolean validRow(Map<String, Cell> row, Set<String> equalsKeys) {
        return row.entrySet().stream()
                .filter(stringCellEntry -> equalsKeys.contains(stringCellEntry.getKey()))
                .map(Map.Entry::getValue)
                .map(Cell::getRows)
                .allMatch(integer -> integer > 0);
    }
}
