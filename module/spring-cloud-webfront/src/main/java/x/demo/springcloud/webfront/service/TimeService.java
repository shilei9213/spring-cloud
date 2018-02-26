package x.demo.springcloud.webfront.service;

public interface TimeService {
    /**
     * 获取当前时间
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    String now();
}
