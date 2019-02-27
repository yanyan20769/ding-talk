package per.yan.ding.model.constant;

/**
 * @author gaoyan
 * @date 2019/2/21 09:28
 */
public class CacheNameSpace {
    public static class Notification {
        /**
         * lock
         */
        public static final String SIMPLE_LOCK = "ovc:notification:simple:lock::{0}";

        public static final String LOCK_RATE_LIMITER = "ovc:notification:lock:rate_limiter:{0}";

        /**
         * redis数据结构
         */
        /**
         * 所有的tokens name-固定  Set<token>
         */
        public static final String DING_SET_ALL_TOKENS = "ovc:notification:ding:set:all_tokens";
        /**
         * 待发消息  name- token, value- List<messageNo>
         */
        public static final String DING_LIST_WAIT_FOR_SEND = "ovc:notification:ding:list:wait_for_send:{0}";
        /**
         * 消息内容   hash name-固定   <k- messageNo，v- DDMessageDO>
         */
        public static final String DING_HASH_MESSAGE_CONTENT = "ovc:notification:ding:hash:message_contend";
        /**
         * 子消息状态 hash name-messageNo  <k- token, v- DDItemMessageDO>
         */
        public static final String DING_HASH_MESSAGE_TOKEN = "ovc:notification:ding:hash:message_token:{0}";

    }
}
