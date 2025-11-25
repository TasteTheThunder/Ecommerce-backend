package EcommerceProject.service;


import EcommerceProject.Exception.APIException;
import EcommerceProject.Exception.ResourceNotFoundException;
import EcommerceProject.Model.Cart;
import EcommerceProject.Model.CartItem;
import EcommerceProject.Model.Product;
import EcommerceProject.payload.CartDTO;
import EcommerceProject.payload.CartItemDTO;
import EcommerceProject.payload.ProductCartDTO;
import EcommerceProject.repositories.CartItemRepository;
import EcommerceProject.repositories.CartRepository;
import EcommerceProject.repositories.ProductRepository;
import EcommerceProject.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please order " + product.getProductName() + " with quantity <= " + product.getQuantity());
        }

        // Find existing cart item
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(), productId);

        if (cartItem != null) {
            // Update quantity
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(quantity);
            cartItem.setDiscount(product.getDiscount());
            cartItem.setProductPrice(product.getSpecialPrice());

            // Add to cart's item list
            cart.getCartItems().add(cartItem);
        }

        // Recalculate total price
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);

        // Save cart (cascade will save items)
        cartRepository.save(cart);

        // Map to DTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        List<CartItemDTO> items = cart.getCartItems().stream().map(ci -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setCartItemId(ci.getCartItemId());
            dto.setProduct(modelMapper.map(ci.getProduct(), ProductCartDTO.class));
            dto.setQuantity(ci.getQuantity());

            // Set original price, discount %, and final price
            dto.setPrice(ci.getProduct().getPrice());      // original price
            dto.setDiscount(ci.getDiscount());             // discount %
            dto.setProductPrice(ci.getProductPrice());     // price after discount

            return dto;
        }).collect(Collectors.toList());

        cartDTO.setItems(items);

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No cart exists");
        }

        return carts.stream().map(cart -> {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setCartId(cart.getCartId());
            cartDTO.setTotalPrice(cart.getTotalPrice());

            List<CartItemDTO> items = cart.getCartItems().stream().map(ci -> {
                CartItemDTO dto = new CartItemDTO();
                dto.setCartItemId(ci.getCartItemId());
                dto.setProduct(modelMapper.map(ci.getProduct(), ProductCartDTO.class));
                dto.setQuantity(ci.getQuantity());

                // Set original price, discount %, and final price
                dto.setPrice(ci.getProduct().getPrice());      // original price
                dto.setDiscount(ci.getDiscount());             // discount %
                dto.setProductPrice(ci.getProductPrice());     // price after discount

                return dto;
            }).collect(Collectors.toList());

            cartDTO.setItems(items);
            return cartDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartByEmail(String emailId) {
        // Fetch cart for this user
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        // Map cart to CartDTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        // Map cart items
        List<CartItemDTO> items = cart.getCartItems().stream().map(ci -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setCartItemId(ci.getCartItemId());
            dto.setProduct(modelMapper.map(ci.getProduct(), ProductCartDTO.class));
            dto.setQuantity(ci.getQuantity());

            // Set original price, discount %, and final price
            dto.setPrice(ci.getProduct().getPrice());      // original price
            dto.setDiscount(ci.getDiscount());             // discount %
            dto.setProductPrice(ci.getProductPrice());     // price after discount

            return dto;
        }).collect(Collectors.toList());


        cartDTO.setItems(items);

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantityChange) {

        // Get logged-in user's cart
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!");
        }

        int newQuantity = cartItem.getQuantity() + quantityChange;

        if (newQuantity > product.getQuantity()) {
            throw new APIException("You can order up to " + product.getQuantity() + " of " + product.getProductName());
        }

        if (newQuantity <= 0) {
            // Remove item if quantity <= 0
            cartItemRepository.delete(cartItem);
            cart.getCartItems().remove(cartItem); // ✅ remove from cart collection
        } else {
            // Update cart item
            cartItem.setQuantity(newQuantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Recalculate total price from all items
        double total = cart.getCartItems().stream()
                .mapToDouble(ci -> ci.getQuantity() * ci.getProductPrice())
                .sum();
        cart.setTotalPrice(total);
        cartRepository.save(cart);

        // Build CartDTO with new structure
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(ci -> {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setCartItemId(ci.getCartItemId());
                    dto.setProduct(modelMapper.map(ci.getProduct(), ProductCartDTO.class));
                    dto.setQuantity(ci.getQuantity());

                    dto.setPrice(ci.getProduct().getPrice());      // original price
                    dto.setDiscount(ci.getDiscount());             // discount %
                    dto.setProductPrice(ci.getProductPrice());     // price after discount

                    return dto;
                }).toList();


        return new CartDTO(cart.getCartId(), cart.getTotalPrice(), itemDTOs);
    }




    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }


    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        // Fetch cart
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        // Fetch product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Fetch cart item for this product
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!");
        }

        // Recalculate cart price: remove old product price, apply updated price
        double updatedTotal = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        // Update cart item with new product details
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setDiscount(product.getDiscount()); // keep discount in sync

        // Add new updated product price
        updatedTotal += cartItem.getProductPrice() * cartItem.getQuantity();

        // Save changes
        cart.setTotalPrice(updatedTotal);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {

        String emailId = authUtil.loggedInEmail();

        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if (existingCart == null) {
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        for (CartItemDTO cartItemDTO : cartItems) {

            // CHANGE HERE ------------
            Long productId = cartItemDTO.getProduct().getProductId();
            // -------------------------

            Integer quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

            totalPrice += product.getSpecialPrice() * quantity;

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());

            cartItemRepository.save(cartItem);
        }

        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);

        return "Cart created/updated with the new items successfully";
    }
}
