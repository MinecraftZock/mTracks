package info.mx.tracks.ops

class UnexpectedHttpStatusException(actualStatus: Int, expectedStatus: Int) : RuntimeException("Expected Status $expectedStatus Actual Status $actualStatus")
