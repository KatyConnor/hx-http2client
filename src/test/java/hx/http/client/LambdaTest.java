package hx.http.client;

import com.alibaba.fastjson.JSONArray;
import hx.http2.client.utils.NumberFormatUtil;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class LambdaTest {

    public static void main(String[] args) {

//        List<Test> list = new ArrayList<>();
//        list.add(new Test("1",26));
//        list.add(new Test("sd",26));
//        list.add(new Test("ff",27));
//        list.add(new Test("1ww",27));
//        list.add(new Test("fds",27));
//        list.add(new Test("aaa",28));
//        list.add(new Test("fff",28));
//        list.add(new Test("ds",29));
//
//        Map<Integer, List<Test>> testMap = list.parallelStream().collect(groupingBy((Test t)-> t.getAge()));
//        System.out.println(JSONArray.toJSONString(testMap));

//        BigDecimal a = new BigDecimal(9.8325000000);
        float a = 9.8325000000f;
        System.out.println(NumberFormatUtil.roundHalfEven(a,3));
    }
}
