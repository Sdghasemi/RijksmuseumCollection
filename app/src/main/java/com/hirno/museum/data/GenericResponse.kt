package com.hirno.museum.data

import com.hirno.museum.network.response.NetworkResponse

/**
 * A typealias used for using generic [ErrorResponseModel] as the ErrorModel type of [NetworkResponse]
 */
typealias GenericResponse<S> = NetworkResponse<S, ErrorResponseModel>