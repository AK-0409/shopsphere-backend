
package com.shopsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(
    name = "Admin",
    description = "APIs for administrative operations"
)
public class AdminController {

    @GetMapping("/test")
    @Operation(
        summary = "Test admin access",
        description = "Verifies that the authenticated user has administrator privileges."
    )
    public ResponseEntity<String> adminTest() {

        return ResponseEntity.ok("Admin access granted");
    }
}
