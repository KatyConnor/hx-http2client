package hx.http2.client.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author andy
 * @date 2018-09-18
 */
public class NumberFormatUtil {

    private static NumberFormat nfFormat = NumberFormat.getInstance();

    /**
     * 四舍六入五成双
     * @param num
     * @param length
     * @return
     */
    public static BigDecimal roundHalfEven(BigDecimal num,int length){
        nfFormat.setMaximumFractionDigits(length);
        return new BigDecimal(nfFormat.format(num));
    }

    /**
     * 四舍六入五成双
     * @param num
     * @param length
     * @return
     */
    public static double roundHalfEven(double num,int length){
        nfFormat.setMaximumFractionDigits(length);
        return Double.valueOf(nfFormat.format(num));
    }

    /**
     * 四舍六入五成双
     * @param num
     * @param length
     * @return
     */
//    public static String roundHalfEven(float num,int length){
//        nfFormat.setMaximumFractionDigits(length);
//        return nfFormat.format(num);
//    }

}
