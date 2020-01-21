package com.server.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun Date.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(time).atZone(ZoneOffset.UTC).toLocalDateTime()
}

fun LocalDateTime.toDate(): Date {
    return Date.from(toInstant(ZoneOffset.UTC))
}