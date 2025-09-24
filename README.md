# **Legy - Food Delivery Platform**

## **📌 Project Overview**
Legy is a **modular monolithic** food delivery platform inspired by services like Glovo. It follows **Domain-Driven Design (DDD)** principles, where each **bounded context** represents a key part of the business logic.

## **🗂 Bounded Contexts & Responsibilities**
Each module (bounded context) is designed to be **independent**, allowing different teams to work separately while ensuring a smooth user experience.

| **Bounded Context**        | **Key Responsibilities** |
|---------------------------|---------------------------|
| **Ordering Context**      | Manages order placement, status updates, and tracking. |
| **Delivery Context**      | Assigns drivers, manages delivery routes, tracking, and QR validation. |
| **Payment Context**       | Handles transactions, refunds, commissions, and fraud detection. |
| **User Context**          | Manages user authentication, roles, and profiles (Guest, Client, Livreur, Admin). |
| **Restaurant Context**    | Manages restaurant profiles, menus, availability, and promotions. |
| **Support Context**       | Handles complaints, fraud alerts, and policy enforcement. |
| **Social Context**        | Enables stories, reels, group interactions, and user-generated content. |
| **Recommendation Context (AI)** | AI-powered personalized meal recommendations & notifications. |
| **Warehouse Context (Future)** | Manages stocks, demand forecasting, supplier management. |
| **Analytics Context**     | Provides KPIs, reporting, A/B testing, and engagement insights. |
| **Notifications Context** | Manages push notifications, email alerts, and SMS updates. |
| **Promotion Context**     | Handles discount codes, special offers, and loyalty programs. |

## **🎯 Main Functionalities Per Module**

### **1️⃣ Ordering Context**
- Users can **place orders** from restaurants.
- Tracks order **status changes** (Pending → Confirmed → Preparing → Out for Delivery → Delivered).
- Manages order **cancellation & refund requests**.

### **2️⃣ Delivery Context**
- Assigns a **livreur (driver)** to orders.
- Manages **delivery tracking**, estimated time of arrival (ETA), and route optimization.
- Provides a **QR validation system** to confirm delivery.

### **3️⃣ Payment Context**
- Handles **secure transactions** via credit card, PayPal, and mobile wallets.
- Manages **refund processing & commission calculations**.
- Implements **fraud detection mechanisms**.

### **4️⃣ User Context**
- Supports **user authentication & authorization**.
- Manages different user **roles** (Guest, Client, Livreur, Admin, Financier, Modérateur).
- Stores user **preferences, addresses, and account settings**.

### **5️⃣ Restaurant Context**
- Enables **restaurant onboarding & management**.
- Handles **menu updates, availability settings, and promotions**.
- Manages restaurant **reviews and ratings**.

### **6️⃣ Support Context**
- Provides a **complaint handling system**.
- Detects **fraudulent activities**.
- Enforces platform **policies & community guidelines**.

### **7️⃣ Social Context**
- Allows users to **post stories & interact with food communities**.
- Supports **group ordering & shared meals**.
- Enables restaurant **social profiles & engagement tracking**.

### **8️⃣ Recommendation Context (AI)**
- AI-based **personalized meal recommendations**.
- **Push notifications** for deals based on user behavior.
- Optimizes search results for better **food discovery**.

### **9️⃣ Warehouse Context (Future)**
- Tracks **inventory levels** for fast-food chains and dark kitchens.
- Optimizes **supply chain & demand forecasting**.

### **🔟 Analytics Context**
- Generates **business insights, KPIs & sales reports**.
- Provides **customer engagement analytics & A/B testing**.

### **1️⃣1️⃣ Notifications Context**
- Sends **real-time order updates, push notifications, and email alerts**.
- Supports **SMS notifications for urgent updates**.

### **1️⃣2️⃣ Promotion Context**
- Manages **promo codes, discounts, and loyalty rewards**.
- Enables **special marketing campaigns**.


### **Branching Strategy**
🔹 **Create a feature branch before adding new functionality:**
```bash
git checkout -b feature-ordering-api
```
🔹 **Push changes to your branch:**
```bash
git push origin feature-ordering-api
```
🔹 **Create a pull request (PR) for code review.**

---
## **📌 Conclusion**
Legy is designed for **scalability, modularity, and collaboration**. By using **Domain-Driven Design (DDD)**, we ensure that each module can evolve independently, enabling efficient team development.

🚀 Happy coding! Let's build something amazing! 🔥

