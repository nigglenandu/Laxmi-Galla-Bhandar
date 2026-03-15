package com.laxmi.galla.core.dto.response;

import java.util.List;

public record GroupedPage<K, V>(K key, List<V> values) {}
