package com.derpgroup.astrobot.util;

public class CacheTuple<K extends Object> {

  private K objectToCache;
  private long ttl = 0;
  
  public void update(K objectToCache, long ttl){
    this.objectToCache = objectToCache;
    this.ttl = ttl;
  }
  
  public K get() {
    return objectToCache;
  }
  
  public long getTtl() {
    return ttl;
  }
  
  public void setTtl(long ttl) {
    this.ttl = ttl;
  }
}
