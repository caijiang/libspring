package me.jiangcai.poi.template.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.jiangcai.poi.template.ExeclEntityRow;
import me.jiangcai.poi.template.IllegalTemplateException;
import me.jiangcai.poi.template.POITemplateService;
import me.jiangcai.poi.template.thymeleaf.POIDialect;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.templateresource.SpringResourceTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class POITemplateServiceImpl implements POITemplateService {

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
    public void export(OutputStream out, BiFunction<Integer, Integer, Iterable<?>> listFunction
            , Resource templateResource, String shellName) throws IOException, IllegalTemplateException
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

        // 数据处理，需将其处理成最小列

        context.setVariable("list", (Iterable) () -> new Iterator() {
            int page = 0;
            Iterator currentIterator;
            Iterator<Map<String, Cell>> listIterator;

            @Override
            public boolean hasNext() {
                // 检查current 有没有
                if (listIterator != null && listIterator.hasNext())
                    return true;
                // 当前有可用的迭代器么？
                if (currentIterator == null) {
                    currentIterator = listFunction.apply(0, SIZE).iterator();
                }
                // 当前的迭代器是否依然有效
                if (currentIterator.hasNext())
                    return true;
                currentIterator = listFunction.apply(++page, SIZE).iterator();
                return currentIterator.hasNext();
            }

            @Override
            public Object next() {
                // 拿到 current 并且current+1
                if (listIterator != null && listIterator.hasNext())
                    return listIterator.next();

                final Object next = currentIterator.next();
                final List<Map<String, Cell>> list;
                if (next instanceof JsonNode) {
                    list = toCellList((JsonNode) next);
                } else if (next instanceof Map) {
                    // name,subList.
                    list = toCellList((Map) next);
                } else if (next.getClass().isArray()) {
                    list = toCellList(next);
                } else if (next instanceof ExeclEntityRow) {
                    list = toCellList((ExeclEntityRow) next);
                } else
                    throw new IllegalArgumentException("unknown type of " + next);
                listIterator = list.iterator();

                return listIterator.next();
            }
        });
        engine.process("", context, new OutputStreamWriter(out, Charset.forName("UTF-8")));
    }

    private List<Map<String, Cell>> toCellList(JsonNode node) {
        return toCellList(node, jsonNode -> IteratorUtils.toList(jsonNode.fieldNames())
                , (jsonNode, o) -> jsonNode.get(o.toString()));
    }

    private List<Map<String, Cell>> toCellList(Object array) {
        return toCellList(array, o -> {
            int length = Array.getLength(o);
            int[] key = new int[length];
            for (int i = 0; i < length; i++) {
                key[i] = i;
            }
            return Arrays.asList(key);
        }, (o, o2) -> Array.get(o, (Integer) o2));
    }

    private List<Map<String, Cell>> toCellList(ExeclEntityRow row) {
        return toCellList(row
                , execlEntityRow -> execlEntityRow.keySet().stream().collect(Collectors.toList())
                , (execlEntityRow, o) -> execlEntityRow.get(o.toString()));
    }

    // 获取该数据的平铺数据

    private List<Map<String, Cell>> toCellList(Map input) {
        return toCellList(input, map -> {
            final Stream<?> stream = map.keySet().stream();
            return stream.collect(Collectors.toList());
        }, Map::get);
    }

    private <T> List<Map<String, Cell>> toCellList(T map, Function<T, List> keyResolver, BiFunction<T, Object, Object> valueResolver) {
        final List set = keyResolver.apply(map);
        // 每列需要的行数
        int[] rows = new int[set.size()];
        for (int i = 0; i < set.size(); i++) {
            Object key = set.get(i);
            Object data = valueResolver.apply(map, key);
            int col = getCol(data);
            rows[i] = col;
        }

        // 总行数
        int max = lcm(rows);
        // 每列的跨行数
        int[] rowSpans = new int[set.size()];
        for (int i = 0; i < rows.length; i++) {
            rowSpans[i] = max / rows[i];
        }

        // 获取到了 开始整理row
        Cell[][] cells = new Cell[max][set.size()];// 暂不支持跨列
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
            for (Map<String, Object> rowData : iterableData) {
                for (String realKey : rowData.keySet()) {
                    Cell cell = new Cell(span, 1, rowData.get(realKey));
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
        return Collections.singletonList(Collections.singletonMap(originKey.toString(), data));
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
