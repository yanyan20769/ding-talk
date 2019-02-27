package per.yan.ding.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NumProducerUtil {

    public static String generateNumWithPrefix(String prefix) {
        if (null == prefix || "".equals(prefix)) {
            throw new IllegalArgumentException("prefix cant be empty");
        }
        final int Min = 1000;
        final int Max = 9999;
        Random random = new Random(java.util.UUID.randomUUID().hashCode());
        int number = random.nextInt((Max - Min) + 1) + Min;
        String date = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        return MessageFormat.format(prefix + "{0}{1}", date, String.valueOf(number));
    }

}
