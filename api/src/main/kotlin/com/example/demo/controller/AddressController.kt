package com.example.demo.controller

import com.example.demo.response.GetAddressResponse
import com.example.demo.usecase.GetAddressUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AddressController(
    private val getAddressUseCase: GetAddressUseCase
) : AbstractController() {

    /**
     * 住所検索
     */
    @GetMapping("/address")
    @ResponseBody
    fun getAddress(@RequestParam("zipcode") zipcode: String): GetAddressResponse {
        return getAddressUseCase.execute(zipcode)
    }
}