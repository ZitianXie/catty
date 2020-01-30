package io.catty.core;

import java.util.HashMap;
import java.util.Map;

public abstract class MappedInvoker implements Invoker {

  protected Map<String, InvokerHolder> invokerMap;

  public MappedInvoker() {
    this(new HashMap<>());
  }

  public MappedInvoker(Map<String, InvokerHolder> invokerMap) {
    this.invokerMap = invokerMap;
  }

  public void setInvokerMap(Map<String, InvokerHolder> invokerMap) {
    this.invokerMap = invokerMap;
  }

  public void registerInvoker(String serverIdentify, InvokerHolder invokerHolder) {
    invokerMap.put(serverIdentify, invokerHolder);
  }
}