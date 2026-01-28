package info.mx.core.ops

class UnexpectedHttpStatusException(actualStatus: Int, expectedStatus: Int) : RuntimeException("Expected Status $expectedStatus Actual Status $actualStatus")
