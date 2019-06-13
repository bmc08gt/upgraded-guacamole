package dev.bmcreations.guacamole.extensions

import com.squareup.picasso.Picasso

fun picasso(cb: ((Picasso) -> Unit)) = cb.invoke(Picasso.get())