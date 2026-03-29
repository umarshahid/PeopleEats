package com.peopleeats.web.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<OrderRecord> listOrders(@RequestParam(required = false) String state) {
        if (state == null || state.isBlank()) {
            return orderRepository.findAll();
        }
        return orderRepository.findByState(state.trim().toUpperCase());
    }

    @PostMapping
    public ResponseEntity<OrderRecord> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        String customer = request.getCustomer().trim();
        String item = request.getItem().trim();
        String orderType = normalizeOrderType(request.getOrderType());

        if (customer.isBlank() || item.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (request.getPrice() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (orderType == null) {
            return ResponseEntity.badRequest().build();
        }

        int orderNo = orderRepository.insert(customer, item, request.getPrice(), orderType, "REQUESTED");
        if (orderNo <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        OrderRecord record = new OrderRecord(orderNo, customer, item, request.getPrice(), orderType, "REQUESTED", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @PatchMapping("/{orderNo}/state")
    public ResponseEntity<OrderRecord> updateState(@PathVariable int orderNo,
                                                   @Valid @RequestBody OrderStateUpdateRequest request) {
        String state = request.getState().trim().toUpperCase();
        if (!isAllowedState(state)) {
            return ResponseEntity.badRequest().build();
        }
        int updated;
        String riderName = request.getRiderName() == null ? "" : request.getRiderName().trim();
        if (!riderName.isBlank() && (state.equals("PREPARING") || state.equals("ON_THE_WAY") || state.equals("READY"))) {
            updated = orderRepository.updateStateWithRider(orderNo, state, riderName);
        } else {
            updated = orderRepository.updateState(orderNo, state);
        }
        if (updated <= 0) {
            return ResponseEntity.notFound().build();
        }

        return orderRepository.findByOrderNo(orderNo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private String normalizeOrderType(String orderType) {
        if (orderType == null) {
            return null;
        }
        String normalized = orderType.trim().toUpperCase();
        if (normalized.equals("DELIVERY") || normalized.equals("NOT DELIVERY") || normalized.equals("NOT_DELIVERY")) {
            return normalized.equals("NOT_DELIVERY") ? "NOT DELIVERY" : normalized;
        }
        return null;
    }

    private boolean isAllowedState(String state) {
        return state.equals("REQUESTED")
                || state.equals("PREPARING")
                || state.equals("READY")
                || state.equals("ON_THE_WAY")
                || state.equals("DELIVERED");
    }
}
