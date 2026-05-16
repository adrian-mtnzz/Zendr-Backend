package com.zendr.backend.api.controllers;

import com.zendr.backend.internal.user.model.*;
import com.zendr.backend.internal.user.model.enums.SubscriptionStatus;
import com.zendr.backend.internal.user.model.enums.UserRole;
import com.zendr.backend.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) {

        User saved = service.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toDTO(saved));
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(service.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/filter")
    public ResponseEntity<UserDTO> getUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String QRCode) {

        Optional<UserDTO> user = Optional.empty();

        if (username != null && !username.isEmpty()) {
            user = service.findByUsername(username);
        }

        if (email != null && !email.isEmpty()) {
            user = service.findByEmail(email);
        }

        if (QRCode != null && !QRCode.isEmpty()) {
            user = service.findByQRCode(QRCode);
        }

        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/filterAll")
    public ResponseEntity<List<UserDTO>> getUsersByDisciplineId(
            @RequestParam(required = false) String disciplineId,
            @RequestParam(required = false) String role) {

        List<UserDTO> users = null;
        if (disciplineId != null && !disciplineId.isEmpty()) {
            users = service.findByDisciplineId(disciplineId);
        }

        if (role != null && !role.isEmpty()) {
            users = service.findByRole(UserRole.valueOf(role));
        }

        if (Objects.requireNonNull(users).isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(
            @PathVariable String id,
            @RequestParam(required = false) Boolean email,
            @RequestParam(required = false) Boolean deportiveProfile,
            @RequestParam(required = false) Boolean penalties,
            @RequestParam(required = false) Boolean billingDetails,
            @RequestParam(required = false) Boolean role,
            @RequestParam(required = false) Boolean dob,
            @RequestParam(required = false) Boolean qrcode) {

        Optional<User> userOpt = service.findByIdRaw(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Map<String, Object> response = new LinkedHashMap<>();

        if (Boolean.TRUE.equals(email)) response.put("email", user.getEmail());
        if (Boolean.TRUE.equals(deportiveProfile)) response.put("deportiveProfile", user.getDeportiveProfile());
        if (Boolean.TRUE.equals(penalties)) response.put("penalties", user.getPenalties());
        if (Boolean.TRUE.equals(billingDetails)) response.put("billingDetails", user.getBillingDetails());
        if (Boolean.TRUE.equals(role)) response.put("role", user.getRole());
        if (Boolean.TRUE.equals(dob)) response.put("dob", user.getDob());
        if (Boolean.TRUE.equals(qrcode)) response.put("QRCode", user.getQRCode());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateUniqueParams(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {

        Map<String, Boolean> result = new HashMap<>();

        if (username != null && !username.isEmpty()) {
            result.put("usernameExists", service.existsByUsername(username));
        }

        if (email != null && !email.isEmpty()) {
            result.put("emailExists", service.existsByEmail(email));
        }

        if (result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/qr-code")
    public ResponseEntity<Map<String, String>> getQrCodeByUserId(@RequestBody Map<String, String> body) {
        
        Map<String, String> response = new HashMap<>();
        response.put("QRCode", service.getQRCode(body.get("id")));

        return ResponseEntity.ok(response);
    }
    
    
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserDTO(@PathVariable String id, @RequestBody UserDTO dto) {

        return service.updateUserFromDTO(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/email")
    public ResponseEntity<String> updateEmail(@PathVariable String id, @RequestParam String email) {

        return service.updateEmail(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PatchMapping("/{id}/username")
    public ResponseEntity<String> updateUsername(@PathVariable String id, @RequestParam String username) {

        return service.updateUsername(id, username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PatchMapping("/{id}/role")
    public ResponseEntity<Map<String, String>> updateRole(@PathVariable String id, @RequestParam String role) {

        
        Map<String, String> response = new HashMap<>();
        response.put("role", service.updateRole(id, UserRole.valueOf(role)).orElseThrow(
                () -> new IllegalArgumentException("No se ha podido actualizar el rol")
        ));
        
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{id}/deportive-profile")
    public ResponseEntity<DeportiveProfile> updateDeportiveProfile(
            @PathVariable String id, @RequestBody DeportiveProfile profile) {

        return service.updateDeportiveProfile(id, profile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/billing-details")
    public ResponseEntity<BillingDetails> updateBillingDetails(
            @PathVariable String id,
            @RequestBody BillingDetails details) {

        return service.updateBillingDetails(id, details)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/penalties/warning")
    public ResponseEntity<Penalties> applyPenalty(@PathVariable String id) {

        return service.applyPenalty(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/penalties/ban")
    public ResponseEntity<Penalties> applyBan(@PathVariable String id) {

        return service.applyBanTrue(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/penalties/un-ban")
    public ResponseEntity<Penalties> applyUnBan(@PathVariable String id) {

        return service.applyBanFalse(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/penalties/reset")
    public ResponseEntity<Penalties> resetPenalties(@PathVariable String id) {

        return service.resetPenalties(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/subscription/status")
    public ResponseEntity<Subscription> updateSubscriptionStatus(
            @PathVariable String id,
            @RequestBody SubscriptionStatus status) {

        Optional<Subscription> sub = switch (status) {
            case ACTIVE -> service.applyActiveSubscription(id);
            case SUSPENDED -> service.applySuspendedSuscription(id);
            case BANNED -> service.applyBannedSubscription(id);
        };

        return sub.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/subscription/self-renewal")
    public ResponseEntity<Subscription> toggleSelfRenewal(@PathVariable String id) {

        return service.toggleSelfRenewal(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
