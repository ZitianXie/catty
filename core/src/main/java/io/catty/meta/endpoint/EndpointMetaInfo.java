package io.catty.meta.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EndpointMetaInfo {

  private Map<String, String> info = new HashMap<>();

  private EndpointTypeEnum endpointTypeEnum;

  public EndpointMetaInfo(EndpointTypeEnum endpointTypeEnum) {
    this.endpointTypeEnum = endpointTypeEnum;
  }

  public EndpointMetaInfo(Map<String, String> info, EndpointTypeEnum endpointTypeEnum) {
    this.info = info;
    this.endpointTypeEnum = endpointTypeEnum;
  }

  public EndpointTypeEnum getEndpointTypeEnum() {
    return endpointTypeEnum;
  }

  // todo : need more robust
  public static EndpointMetaInfo parse(String metaInfoStr, EndpointTypeEnum endpointTypeEnum) {
    Map<String, String> map = new HashMap<>();
    String[] metaInfoEntryArray = metaInfoStr.split(";");
    for(String entry : metaInfoEntryArray) {
      if(entry == null || "".equals(entry)) {
        continue;
      }
      map.put(entry.substring(0, entry.indexOf("=")), entry.substring(entry.indexOf("=") + 1));
    }
    return new EndpointMetaInfo(map, endpointTypeEnum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(Entry<String, String> entry : info.entrySet()) {
      sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
    }
    return sb.toString();
  }

  public void addMetaInfo(String key, String value) {
    info.put(key, value);
  }

  public byte getByte(String key) {
    return Byte.valueOf(info.get(key));
  }

  public short getShort(String key) {
    return Short.valueOf(info.get(key));
  }

  public int getInt(String key) {
    return Integer.valueOf(info.get(key));
  }

  public long getLong(String key) {
    return Long.valueOf(info.get(key));
  }

  public String getString(String key) {
    return info.get(key);
  }

  public boolean getBool(String key) {
    return Boolean.valueOf(info.get(key));
  }

  public double getDouble(String key) {
    return Double.valueOf(info.get(key));
  }

  public byte getByteDef(String key, byte def) {
    if(info.containsKey(key)) {
      return Byte.valueOf(info.get(key));
    }
    return def;
  }

  public short getShortDef(String key, short def) {
    if(info.containsKey(key)) {
      return Short.valueOf(info.get(key));
    }
    return def;
  }

  public int getIntDef(String key, int def) {
    if(info.containsKey(key)) {
      return Integer.valueOf(info.get(key));
    }
    return def;
  }

  public long getLongDef(String key, long def) {
    if(info.containsKey(key)) {
      return Long.valueOf(info.get(key));
    }
    return def;
  }

  public String getStringDef(String key, String def) {
    if(info.containsKey(key)) {
      return info.get(key);
    }
    return def;
  }

  public boolean getBoolDef(String key, boolean def) {
    if(info.containsKey(key)) {
      return Boolean.valueOf(info.get(key));
    }
    return def;
  }

  public double getDoubleDef(String key, double def) {
    if(info.containsKey(key)) {
      return Double.valueOf(info.get(key));
    }
    return def;
  }

  @Override
  public int hashCode() {
    return info.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof EndpointMetaInfo)) {
      return false;
    }
    return info.equals(((EndpointMetaInfo) o).info);
  }

}