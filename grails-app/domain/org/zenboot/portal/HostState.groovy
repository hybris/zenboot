package org.zenboot.portal

enum HostState {
    //ATTENTION: order is used in comparisons to identify a lower or advances state! Don't change this order except you have a valid reason.
    UNKNOWN,
    REQUESTING,
    CREATED,
    ACCESSIBLE,
    COMPLETED,
    DISABLED,
    DELETED,
    BROKEN,
    UNMANAGED
}
