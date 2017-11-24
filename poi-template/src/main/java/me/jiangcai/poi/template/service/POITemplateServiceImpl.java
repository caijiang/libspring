package me.jiangcai.poi.template.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.jiangcai.poi.template.ExeclEntityRow;
import me.jiangcai.poi.template.IllegalTemplateException;
import me.jiangcai.poi.template.POITemplateService;
import me.jiangcai.poi.template.SingleValue;
import me.jiangcai.poi.template.thymeleaf.POIDialect;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.templateresource.SpringResourceTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.sql.Time;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class POITemplateServiceImpl implements POITemplateService {
    private static final Log log = LogFactory.getLog(POITemplateServiceImpl.class);

    private static final int SIZE = 20;

    private static int gcd(int a, int b) {
        if (a < b) return gcd(b, a);
        if (a % b == 0) return b;
        else return gcd(b, a % b);
    }

    public static int lcm(int... input) {
        if (input.length == 0)
            throw new IllegalArgumentException("");
        if (input.length == 1)
            return input[0];
        int[] newArray = new int[input.length - 1];
        System.arraycopy(input, 1, newArray, 0, newArray.length);
        int a = input[0];
        int b = lcm(newArray);
        return ((a * b) / gcd(a, b));
    }

    @Override
    public void export(OutputStream out, Supplier<List<?>> listSupplier, Function<Pageable, Page<?>> pageFunction
            , Set<String> equalsKeys, Set<String> allowKeys, Resource templateResource, String shellName) throws IOException, IllegalTemplateException
            , IllegalArgumentException {


        // 弄一个 thymeleaf 的 的xml引擎
        TemplateEngine engine = new TemplateEngine();
//        engine.addDialect();
        // 只解析我特定的
        engine.setTemplateResolver(new SpringResourceTemplateResolver() {
            @Override
            protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
                return new SpringResourceTemplateResource(templateResource, "UTF-8");
            }
        });

        engine.addDialect(new POIDialect());

        Context context = new Context();

        final List<?> onlyList;
        if (listSupplier != null) {
            onlyList = listSupplier.get();
        } else {
            onlyList = null;
        }

        // 数据处理，需将其处理成最小列
        final Iterable<?> listTotal = () -> {
            Iterator iterator = new Iterator() {
                int page = 0;
                Iterator currentIterator;
                Iterator<Map<String, Cell>> listIterator;

                @Override
                public boolean hasNext() {
                    // 检查current 有没有
                    if (listIterator != null && listIterator.hasNext())
                        return true;
                    if (onlyList != null) {
                        if (currentIterator != null)
                            return false;
                        currentIterator = onlyList.iterator();
                        context.setVariable("_listTotal", onlyList.size());
                        return currentIterator.hasNext();
                    }
                    // 当前有可用的迭代器么？
                    if (currentIterator == null) {
                        final Page<?> page = pageFunction.apply(new PageRequest(0, SIZE));
                        context.setVariable("_listTotal", page.getTotalElements());
                        currentIterator = page.iterator();
                    }
                    // 当前的迭代器是否依然有效
                    if (currentIterator.hasNext())
                        return true;
                    final Page<?> apply = pageFunction.apply(new PageRequest(++page, SIZE));
                    if (apply == null)
                        return false;
                    currentIterator = apply.iterator();
                    return currentIterator.hasNext();
                }

                @Override
                public Object next() {
                    // 拿到 current 并且current+1
                    if (listIterator != null && listIterator.hasNext())
                        return listIterator.next();

                    // 先持续滴next 然后把所有list进行合并。
                    final List<Map<String, Cell>> list = new ArrayList<>();
                    while (true) {
                        try {
                            final Object next = currentIterator.next();
                            final List<Map<String, Cell>> subList;
                            if (next instanceof JsonNode) {
                                subList = toCellList((JsonNode) next, allowKeys, context);
                            } else if (next instanceof Map) {
                                // name,subList.
                                subList = toCellList((Map) next, allowKeys, context);
                            } else if (next.getClass().isArray()) {
                                subList = toCellListFromArray(next, allowKeys, context);
                            } else if (next instanceof ExeclEntityRow) {
                                subList = toCellList((ExeclEntityRow) next, allowKeys, context);
                            } else
                                subList = toCellList(next, allowKeys, context);

                            list.addAll(subList);
                        } catch (NoSuchElementException ignored) {
                            break;
                        }
                    }


                    // 将根据 equalsKey 进行重组
                    final List<Map<String, Cell>> setupList;
                    if (equalsKeys != null && !equalsKeys.isEmpty()) {
                        //  第一步寻找一致的key 而且必须是临近的
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

                        Map<String, Cell> lastHitRow = null;
                        Integer lastHash = Integer.MAX_VALUE;

                        setupList = new ArrayList<>(list.size());
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
                            setupList.add(row);
                        }
                    } else
                        setupList = list;

                    if (log.isDebugEnabled()) {
                        setupList.forEach(stringCellMap -> {
                            StringBuilder rs = new StringBuilder();
                            stringCellMap.entrySet().forEach(stringCellEntry -> {
                                rs.append(stringCellEntry.getKey()).append(":").append(stringCellEntry.getValue()).append(",");
                            });
                            log.debug(rs.toString());
                        });
                    }

                    listIterator = setupList.iterator();

                    return listIterator.next();
                }
            };
//            iterator.hasNext();
            return iterator;
        };

        final Iterator iterator = listTotal.iterator();
        final int rows;
        if (iterator.hasNext()) {
            iterator.next();
            rows = (Integer) context.getVariable("rowsPerRow")
                    * ((Number) context.getVariable("_listTotal")).intValue();
        } else {
            rows = 0;
        }

        context.setVariable("rows", rows);
        context.setVariable("list", listTotal);
        engine.process("", context, new OutputStreamWriter(out, Charset.forName("UTF-8")));
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

    private List<Map<String, Cell>> toCellList(Object object, Set<String> allowKeys, Context context) {
        return toCellList(object, allowKeys, beanKeys(), beanValueResolver(), context);
    }

    private BiFunction<Object, Object, Object> beanValueResolver() {
        return (o, o2) -> {
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(o.getClass(), o2.toString());
            try {
                return propertyDescriptor.getReadMethod().invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    private Function<Object, List<?>> beanKeys() {
        return o -> Stream.of(BeanUtils.getPropertyDescriptors(o.getClass()))
                .filter(propertyDescriptor -> propertyDescriptor.getReadMethod() != null)
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equalsIgnoreCase("class"))
                .map(PropertyDescriptor::getName)
                .collect(Collectors.toList());
    }

    private List<Map<String, Cell>> toCellList(JsonNode node, Set<String> allowKeys, Context context) {
        return toCellList(node, allowKeys, jsonNode -> IteratorUtils.toList(jsonNode.fieldNames())
                , (jsonNode, o) -> jsonNode.get(o.toString()), context);
    }

    private List<Map<String, Cell>> toCellListFromArray(Object array, Set<String> allowKeys, Context context) {
        return toCellList(array, allowKeys, o -> {
            int length = Array.getLength(o);
            ArrayList<Integer> keys = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                keys.add(i);
            }
            return keys;
        }, (o, o2) -> Array.get(o, (Integer) o2), context);
    }

    private List<Map<String, Cell>> toCellList(ExeclEntityRow row, Set<String> allowKeys, Context context) {
        return toCellList(row
                , allowKeys, execlEntityRow -> execlEntityRow.keySet().stream().collect(Collectors.toList())
                , (execlEntityRow, o) -> execlEntityRow.get(o.toString()), context);
    }

    // 获取该数据的平铺数据

    private List<Map<String, Cell>> toCellList(Map input, Set<String> allowKeys, Context context) {
        return toCellList(input, allowKeys, map -> {
            final Stream<?> stream = map.keySet().stream();
            return stream.collect(Collectors.toList());
        }, Map::get, context);
    }

    private <T> List<Map<String, Cell>> toCellList(T map, Set<String> allowKeys, Function<T, List<?>> keyResolver
            , BiFunction<T, Object, Object> valueResolver, Context context) {
        final List<?> originSet = keyResolver.apply(map);
        final List<?> set;
        if (allowKeys != null) {
            set = originSet.stream()
                    .filter(allowKeys::contains)
                    .collect(Collectors.toList());
        } else
            set = originSet;

        // 每列需要的行数
        int[] rows = new int[set.size()];
        for (int i = 0; i < set.size(); i++) {
            Object key = set.get(i);
            Object data = valueResolver.apply(map, key);
            int col = getCol(data);
            rows[i] = Math.max(col, 1);
        }

        // 总行数
        int max = lcm(rows);
        // 只留下最大的。
        if (!context.containsVariable("rowsPerRow"))
            context.setVariable("rowsPerRow", max);
        else
            context.setVariable("rowsPerRow", Math.max(max, (int) context.getVariable("rowsPerRow")));
        // 每列的跨行数
        int[] rowSpans = new int[set.size()];
        for (int i = 0; i < rows.length; i++) {
            rowSpans[i] = max / rows[i];
        }

        // 获取到了 开始整理row
        @SuppressWarnings("unchecked")
        Map<String, Cell>[] lists = new Map[max];
        Arrays.setAll(lists, (IntFunction<Map<String, Cell>>) value -> new HashMap<>());

        for (int i = 0; i < set.size(); i++) {
            Object key = set.get(i);
            Object data = valueResolver.apply(map, key);
            int span = rowSpans[i];
            // 跨则同一个
            // 取出一个子data
//            final Map<String, Object> iterable = iterable(key, data);
            final List<Map<String, Object>> iterableData = iterable(key, data);
            int currentRow = 0;
            if (iterableData.isEmpty()) {
                lists[currentRow++].put(key.toString(),new Cell("",span,1));
                for (int k = 0; k < span - 1; k++) {
//                    for (String realKey : rowData.keySet()) {
                    lists[currentRow].put(key.toString(), Cell.EMPTY);
//                    }
                    currentRow++;
                }
            } else {
                for (Map<String, Object> rowData : iterableData) {
                    for (String realKey : rowData.keySet()) {
                        Cell cell = new Cell(rowData.get(realKey), span, 1);
                        // currentRow realKey 的cell确定了
                        lists[currentRow].put(realKey, cell);
                    }
                    currentRow++;
                    // 还剩下 span -1
                    for (int k = 0; k < span - 1; k++) {
                        for (String realKey : rowData.keySet()) {
                            lists[currentRow].put(realKey, Cell.EMPTY);
                        }
                        currentRow++;
                    }
                    // 如果已经写到最后一行 说明该列需要写入新的一列 则重新开启
                    if (currentRow == max) {
                        currentRow = 0;
                    }
                }
            }

            // iterableData
//            int currentRow = 0;
//            for (String realKey : iterable.keySet()) {
//                Cell cell = new Cell(span, 1, iterable.get(realKey));
//                // currentRow realKey 的cell确定了
//                lists[currentRow++].put(realKey, cell);
//                // 还剩下 span -1
//                for (int k = 0; k < span - 1; k++) {
//                    lists[currentRow++].put(realKey, Cell.EMPTY);
//                }
//            }
        }

        return Arrays.asList(lists);
    }

    /**
     * 长度需要占用的高度
     *
     * @param originKey 原始字段
     * @param data      该字段的内容
     * @return 若为对象 则直接 key.dataName 逐一平铺；若为数组 则
     */
    private List<Map<String, Object>> iterable(Object originKey, Object data) {
        if (data == null)
            return Collections.singletonList(Collections.singletonMap(originKey.toString(), null));
        if (data instanceof Cell)
            return Collections.singletonList(Collections.singletonMap(originKey.toString(), ((Cell) data).getValue()));
        if (data instanceof JsonNode && ((JsonNode) data).isObject()) {
            // 是json而且是个jsonObject
            HashMap<String, Object> array = new HashMap<>();
            final Iterator<Map.Entry<String, JsonNode>> fields = ((JsonNode) data).fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                array.put(entry.getKey(), entry.getValue());
            }
            return iterable(originKey, array);
        }
        if (data instanceof JsonNode && ((JsonNode) data).isArray()) {
            // 是json而且是个json array
            // 将它的值取出! 成为一个Array
            final Object[] array = new Object[((JsonNode) data).size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = ((JsonNode) data).get(i);
            }
            return iterable(originKey, array);
        }
        if (data instanceof Map) {
            @SuppressWarnings("unchecked") final Stream<Map.Entry> stream = ((Map) data).entrySet().stream();
            return Collections.singletonList(stream
                    .collect(Collectors.toMap(o -> originKey + "." + o.getKey()
                            , Map.Entry::getValue)));
        }
        if (data instanceof Collection) {
            final Collection collection = (Collection) data;
            final Object[] array = new Object[collection.size()];
            collection.toArray(array);
            return iterable(originKey, array);
        }
        if (data.getClass().isArray()) {
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < Array.getLength(data); i++) {
                List<Map<String, Object>> itemData = iterable(originKey, Array.get(data, i));
                if (itemData.size() != 1)
                    throw new IllegalStateException("内部又产生循环？" + itemData);
                list.add(itemData.get(0));
            }
            return list;
        }
//        if (beanAccepted)
//            return Collections.singletonList(Collections.singletonMap(originKey.toString(), data));

        // 如果是简单的直接给，复杂的 变成map给吧。。
        if (data.getClass().isPrimitive()
                || data.getClass() == String.class
                || Number.class.isAssignableFrom(data.getClass())
                || Temporal.class.isAssignableFrom(data.getClass())
                || Date.class.isAssignableFrom(data.getClass())
                || Time.class.isAssignableFrom(data.getClass())
                || SingleValue.class.isAssignableFrom(data.getClass())
                || JsonNode.class.isAssignableFrom(data.getClass()))
            return Collections.singletonList(Collections.singletonMap(originKey.toString(), data));

        // into a map
        HashMap<Object, Object> map = new HashMap<>();
        beanKeys().apply(data).forEach(key -> map.put(key, beanValueResolver().apply(data, key)));
        return iterable(originKey, map);
    }

    private int getCol(Object data) {
        int col;
        if (data == null)
            col = 1;
        else if (data instanceof Cell)
            col = 1;
        else if (data instanceof JsonNode) {
            // 里面的数据 如果是数组的话
            if (((JsonNode) data).isArray())
                return ((JsonNode) data).size();
            return 1;
        } else if (data instanceof Map) {
//            col = ((Map) data).size();
            col = 1;
        } else if (data instanceof Collection) {
            col = ((Collection) data).size();
        } else if (data.getClass().isArray()) {
            col = Array.getLength(data);
        } else
            col = 1;
        return col;
    }

}
