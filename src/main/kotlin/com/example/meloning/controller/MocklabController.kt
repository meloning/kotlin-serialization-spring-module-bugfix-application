package com.example.meloning.controller

import com.example.meloning.service.MockLabService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class MocklabController(
    private val mockLabService: MockLabService
) {

    @GetMapping("/v1/mock-lab")
    fun getUserMockLab(): ResponseEntity<Unit> {
        mockLabService.createMockLabTester()
        return ResponseEntity.ok().build()
    }
}