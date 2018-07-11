package com.twillms.sangria.model

import sangria.validation.Violation

object DateTimeCoerceViolation extends Violation {

    override def errorMessage: String = "Error while parsing date and time."

}
