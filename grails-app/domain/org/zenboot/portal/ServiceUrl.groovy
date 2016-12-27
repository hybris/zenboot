package org.zenboot.portal

class ServiceUrl implements Comparable<ServiceUrl> {

  String url
  static belongsTo = [owner:Host]

  @Override
  int compareTo(ServiceUrl o) {
    this.url <=> o.url
  }
}
