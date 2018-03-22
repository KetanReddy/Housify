package app.housify.util

import app.housify.auth.AuthType
import io.javalin.Context

fun Context.getAuthType(): AuthType {
    // By reading the ID and comparing it to that in the db,
    // we could determine what level of access this user should have
    return AuthType.NONE
}