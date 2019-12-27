package dev.bmcreations.musickit.networking.api.models

abstract class PagedListImpl<T>(val data : List<T>? = null, val next: String? = null)
