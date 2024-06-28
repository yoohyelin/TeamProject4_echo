package com.project.echoproject.service;

import com.project.echoproject.dto.order.BuyerDTO;
import com.project.echoproject.dto.order.ItemDTO;
import com.project.echoproject.dto.order.OrderDTO;
import com.project.echoproject.entity.*;
import com.project.echoproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SiteUserRepository siteUserRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Order createOrder(String orderNumber, String buyerTel, String buyerEmail, String buyerAddr, String buyerPostcode, int totalAmount, String userId) {
        SiteUser siteUser = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUser(siteUser).orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setBuyer(siteUser);
        order.setBuyerTel(buyerTel);
        order.setBuyerEmail(buyerEmail);
        order.setBuyerAddr(buyerAddr);
        order.setBuyerPostcode(buyerPostcode);
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        orderRepository.save(order);


        return order;
    }

    @Override
    public OrderDTO getOrderDetails(String orderNumber) {
        // 주문 정보를 데이터베이스에서 조회
        Order order = orderRepository.findByOrderNumber(orderNumber);

        // Order가 null인 경우 처리
        if (order == null) {
            // 예외를 던지거나, 빈 OrderDTO를 반환하거나, 적절한 방법으로 처리
            throw new RuntimeException("Order not found for orderNumber: " + orderNumber);
        }

        // Order 엔티티를 OrderDTO로 변환
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderNumber(order.getOrderNumber());
        orderDTO.setTotalAmount(order.getTotalAmount());

        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setUsername(order.getBuyer().getUserId());
        buyerDTO.setBuyerTel(order.getBuyer().getPhoneNum());
        buyerDTO.setBuyerAddr(order.getBuyer().getAddress());
        buyerDTO.setBuyerPostcode(order.getBuyer().getAddress());
        orderDTO.setBuyer(buyerDTO);

        // Items가 null이 아닌지 확인하고 변환
        if (order.getItems() != null) {
            List<ItemDTO> itemDTOList = order.getItems().stream().map(item -> {
                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setProductName(item.getProduct().getProductName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getProduct().getPrice());
                return itemDTO;
            }).collect(Collectors.toList());
            orderDTO.setItems(itemDTOList);
        } else {
            // Items가 null인 경우 빈 리스트로 설정
            orderDTO.setItems(Collections.emptyList());
        }

        return orderDTO;
    }

    @Transactional
    @Override
    public void clearCart(SiteUser siteUser) {
        Cart cart = cartRepository.findByUser(siteUser).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

}
