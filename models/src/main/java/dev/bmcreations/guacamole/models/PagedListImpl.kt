package dev.bmcreations.guacamole.models

abstract class PagedListImpl<T>(val data : List<T>? = null, val next: String? = null)
