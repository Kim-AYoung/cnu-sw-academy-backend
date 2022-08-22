package org.prgms.kdt.springbootjpalecture.order.controller;

import lombok.RequiredArgsConstructor;
import org.prgms.kdt.springbootjpalecture.ApiResponse;
import org.prgms.kdt.springbootjpalecture.domain.order.Order;
import org.prgms.kdt.springbootjpalecture.order.dto.OrderDto;
import org.prgms.kdt.springbootjpalecture.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @ExceptionHandler
    private ApiResponse<String> exceptionHandle(Exception exception) {
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

//    @ExceptionHandler(Exception.class)
//    private ApiResponse<String> notFoundHandle(Exception exception) {
//        return ApiResponse.fail(HttpStatus.NOT_FOUND.value(), exception.getMessage());
//    }

    @PostMapping("/orders")
    public ApiResponse<String> save(@RequestBody OrderDto orderDto) {
        return ApiResponse.ok(orderService.save(orderDto));
    }

    @PostMapping("/orders/{uuid}")
    public ApiResponse<String> update(
            @PathVariable String uuid,
            @RequestBody OrderDto orderDto
    ) throws Exception {
        return ApiResponse.ok(orderService.update(uuid, orderDto));
    }

    @GetMapping("/orders/{uuid}")
    public ApiResponse<OrderDto> getOne(
            @PathVariable String uuid
    ) throws Exception {
        return ApiResponse.ok(orderService.findOne(uuid));
    }

    @GetMapping("/orders")
    public ApiResponse<Page<OrderDto>> getAll(Pageable pageable) {
        return ApiResponse.ok(orderService.findOrders(pageable));
    }
}
