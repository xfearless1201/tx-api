package com.cn.tianxia.api.base.datashource;

public class DataSourceHolder {

  private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

  /**
   * 获得当前线程数据源
   *
   * @return 数据源名称
   */
  public static String peek() {
      return CONTEXT_HOLDER.get();
  }

  /**
   * 设置当前线程数据源
   *
   * @param dataSource 数据源名称
   */
  public static void push(String dataSource) {
      CONTEXT_HOLDER.set(dataSource);
  }

  /**
   * 清空当前线程数据源
   */
  public static void poll() {
      CONTEXT_HOLDER.remove();
  }
}
