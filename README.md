# 🛒 Ecommerce Backend — Spring Boot

A complete **E-commerce Backend** built with **Spring Boot**, implementing authentication (JWT), product management, categories, cart, orders, user roles, file uploads, and more.  
Designed using clean layered architecture with DTOs, services, repositories, and centralized exception handling.

This backend is suitable for real-world e-commerce applications and excellent for placement/internship showcase.

---

## 🚀 Features

### 🔐 Authentication & User Management
- User Registration (Signup)
- Login with **JWT Token**
- Role-based access — `USER`, `ADMIN`
- View Profile Endpoint (`/me`)
- Secure endpoints using Spring Security + JWT
- Custom exception handling for unauthorized access

### 📦 Product Management
- Create, update, delete products (**Admin only**)
- Fetch all products
- Fetch single product
- Pagination & Sorting support
- Product-Category relationship
- Product image upload + static file serving

### 🗂️ Category Management
- Add, update, delete categories
- Assign categories to products
- Fetch all categories

### 🛒 Cart Module
- Add items to cart
- Remove items from cart
- Update quantity
- Auto-calculate total cart value
- One cart per user

### 🧾 Orders
- Convert cart to order
- Order history for user
- Admin: fetch all orders
- Payment & delivery details stored

### 🖼️ Media Handling
- Static image handling via `/uploads/**`
- File storage configured in `WebMvcConfig`

---

## 📦 Project Structure

EcommerceProject/
├── Controller/
│ ├── AuthController.java
│ ├── ProductController.java
│ ├── CategoryController.java
│ ├── CartController.java
│ └── OrderController.java
│
├── Model/
│ ├── User.java
│ ├── Role.java
│ ├── Product.java
│ ├── Category.java
│ ├── Cart.java
│ ├── CartItem.java
│ ├── Order.java
│ ├── Address.java
│ └── Payment.java
│
├── Security/
│ ├── jwt/
│ │ ├── AuthTokenFilter.java
│ │ ├── JwtUtils.java
│ │ └── EntryPointJwt.java
│ │
│ ├── services/
│ │ ├── UserDetailsServiceImpl.java
│ │ └── WebSecurityConfig.java
│ │
│ ├── request/
│ │ ├── LoginRequest.java
│ │ └── SignupRequest.java
│ │
│ └── response/
│ ├── UserInfoResponse.java
│ └── MessageResponse.java
│
├── payload/
│ ├── ProductDTO.java
│ ├── ProductResponse.java
│ ├── CategoryDTO.java
│ ├── CartDTO.java
│ ├── OrderDTO.java
│ └── PaginationResponse.java
│
├── repositories/
│ ├── UserRepository.java
│ ├── ProductRepository.java
│ ├── CategoryRepository.java
│ ├── CartRepository.java
│ ├── OrderRepository.java
│ └── RoleRepository.java
│
├── service/
│ ├── ProductServiceImpl.java
│ ├── CategoryServiceImpl.java
│ ├── CartServiceImpl.java
│ ├── OrderServiceImpl.java
│ └── AuthServiceImpl.java
│
├── Exception/
│ ├── GlobalExceptionHandler.java
│ └── ResourceNotFoundException.java
│
└── config/
├── SwaggerConfig.java
├── WebMvcConfig.java
└── AppConstants.java
---

## 🛢️ Database Entities

- **User**
- **Role**
- **Product**
- **Category**
- **Cart & CartItem**
- **Order**
- **Address**
- **Payment**

Relationships used:
- One-to-Many
- Many-to-One
- Many-to-Many (User ↔ Roles)
- Cascade operations (where required)

---

## 📌 API Endpoints Overview

### 🔐 Auth Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login + JWT |
| GET | `/api/auth/me` | Get logged-in user |

### 📦 Product Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/` | Get all products |
| GET | `/api/products/{id}` | Get single product |
| POST | `/api/products/` | Add product (Admin) |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### 🗂️ Category Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories/` | Get categories |
| POST | `/api/categories/` | Add category |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

### 🛒 Cart Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cart/add` | Add item to cart |
| GET | `/api/cart/{id}` | Get user cart |
| PUT | `/api/cart/update` | Update quantity |
| DELETE | `/api/cart/remove/{itemId}` | Remove item |

### 🧾 Order Routes
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/` | Place order |
| GET | `/api/orders/user` | User order history |
| GET | `/api/orders/all` | All orders (Admin) |

---

## ⚙️ How to Run Locally

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/TasteTheThunder/Ecommerce-backend.git
cd Ecommerce-backend

